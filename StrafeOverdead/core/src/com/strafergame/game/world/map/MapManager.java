package com.strafergame.game.world.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.factories.EntityRegistry;
import com.strafergame.game.ecs.factories.MapEntityFactory;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarPathfinder;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DWorld;

import java.util.HashMap;
import java.util.Map;

public class MapManager {

    private static MapManager instance;
    private Box2DWorld box2DWorld;
    private RayHandler rayHandler;
    private TiledMap map;
    private static int maxElevation = 0;

    private static final HashMap<Integer, MapLayers> layersElevatedMap = new HashMap<>();
    private static final HashMap<Integer, AStarPathfinder> pathfinders = new HashMap<>();
    public static int width;
    public static int height;

    public MapManager(Box2DWorld box2dworld, RayHandler rayHandler) {
        instance = this;
        this.box2DWorld = box2dworld;
        this.rayHandler = rayHandler;

    }

    public static MapManager getInstance() {
        return instance;
    }

    public void loadMap(TiledMap tiledMap) {
        if (this.map != null) {
            this.map.dispose();
        }
        layersElevatedMap.clear();
        pathfinders.clear();
        layersElevatedMap.put(0, new MapLayers());
        maxElevation = 0;


        this.map = tiledMap;

        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                width = ((TiledMapTileLayer) layer).getWidth();
                height = ((TiledMapTileLayer) layer).getHeight();
                break;
            }
        }

        tiledMap.getLayers().forEach(MapEntityFactory::createLayerEntity);
        loadMapObjects(tiledMap);
        layersElevatedMap.put(maxElevation + 1, new MapLayers());   //to allow jump at peak height

    }

    public static AStarPathfinder getPathfinder(int elevation) {
        AStarPathfinder pf = pathfinders.get(elevation);
        if (pf == null) {
            pf = new AStarPathfinder(width, height, elevation);
            pathfinders.put(elevation, pf);
        }
        return pf;
    }

    private void loadMapObjects(TiledMap tiledMap) {
        Strafer.worldCamera.setFocusOn(GameWorld.player);
        Strafer.tiledMapRenderer.setMap(tiledMap);

        map.getLayers().forEach(layer -> {
            String name = layer.getName();
            String typeStr = layer.getProperties().get("type", String.class);
            if (typeStr == null) {
                // Try to infer type from name if property not set
                if (name.startsWith("collisions")) typeStr = "collision";
                else if (name.startsWith("elevationAgents")) typeStr = "elevationAgent";
                else if (name.startsWith("checkpoints")) typeStr = "checkpoint";
                else if (name.startsWith("enemies")) typeStr = "goblin";
            }

            final EntityType type = EntityType.convert(typeStr);
            if (type != null) {
                if (layer instanceof TiledMapTileLayer) {
                    loadTileLayer(tiledMap, name, (i, j) -> {
                        EntityRegistry.create(type, new Vector3(i, j, 0), null);
                    });
                    if (EntityType.isEnemyOrNPC(type)) {
                        layer.setVisible(false);
                    }
                } else {
                    loadObjectLayer(tiledMap, name, mapObject -> {
                        EntityRegistry.create(type, null, mapObject);
                    });
                }
            }
        });
    }

    private void loadTileLayer(TiledMap map, String layerName, TileLayerLoadAction lla) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        for (int i = 1; i <= layer.getWidth(); i++) {
            for (int j = 1; j <= layer.getHeight(); j++) {
                if (layer.getCell(i, j) != null) {
                    lla.execute(i, j);
                }
            }
        }
    }

    private void loadObjectLayer(TiledMap map, String layerName, ObjectLayerLoadAction lla) {
        MapObjects objects = map.getLayers().get(layerName).getObjects();
        for (MapObject mapObject : objects) {
            lla.execute(mapObject);
        }
    }

    /**
     * returns all of the TiledMap's layers that have the given elevation
     */
    public static MapLayers getLayersElevatedMap(int elevation) {
        return layersElevatedMap.get(elevation);
    }

    /**
     * populates the hashmap
     */
    public static void addLayerToElevation(MapLayer layer, int elevation) {
        if (elevation > MapManager.maxElevation) {
            MapManager.maxElevation = elevation;
            MapManager.getLayersElevatedMap().put(elevation, new MapLayers());
        }
        if (MapManager.getLayersElevatedMap().get(elevation) != null) {
            MapManager.getLayersElevatedMap().get(elevation).add(layer);
        }
    }

    public static MapLayers getLayersBelow(int elevation) {
        MapLayers rez = new MapLayers();
        for (; elevation >= 0; elevation--) {
            for (MapLayer layer : getLayersElevatedMap(elevation)) {
                rez.add(layer);
            }
        }
        return rez;
    }

    public static MapLayers getLayersAbove(int elevation) {
        MapLayers rez = new MapLayers();
        for (; elevation <= maxElevation; elevation++) {
            for (MapLayer layer : getLayersElevatedMap(elevation)) {
                rez.add(layer);
            }
        }
        return rez;
    }

    public static int getMaxElevation() {
        return maxElevation;
    }

    public static Map<Integer, MapLayers> getLayersElevatedMap() {
        return layersElevatedMap;
    }

    public TiledMap getMap() {
        return map;
    }


}
