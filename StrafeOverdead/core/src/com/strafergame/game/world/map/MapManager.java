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
import com.strafergame.game.ecs.factories.EntityFactory;
import com.strafergame.game.ecs.factories.MapEntityFactory;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DWorld;

import java.util.HashMap;
import java.util.Map;

public class MapManager {

    private Box2DWorld box2DWorld;
    private RayHandler rayHandler;
    private TiledMap map;
    private static int maxElevation = 0;

    private static final HashMap<Integer, MapLayers> layersElevatedMap = new HashMap<>();

    public MapManager(Box2DWorld box2dworld, RayHandler rayHandler) {
        this.box2DWorld = box2dworld;
        this.rayHandler = rayHandler;

    }

    public void loadMap(TiledMap tiledMap) {
        if (this.map != null) {
            this.map.dispose();
        }
        layersElevatedMap.clear();
        layersElevatedMap.put(0, new MapLayers());
        maxElevation = 0;


        this.map = tiledMap;

        tiledMap.getLayers().forEach(MapEntityFactory::createLayerEntity);
        loadMapObjects(tiledMap);
        layersElevatedMap.put(maxElevation + 1, new MapLayers());   //to allow jump at peak height

    }

    private void loadMapObjects(TiledMap tiledMap) {

        Strafer.worldCamera.setFocusOn(GameWorld.player);

        Strafer.tiledMapRenderer.setMap(tiledMap);

        map.getLayers().forEach(layer -> {
            String name = layer.getName();
            if (name.startsWith("collisions")) {
                loadObjectLayer(tiledMap, name, mapObject -> {
                    MapEntityFactory.createCollisionEntity(box2DWorld.getWorld(), mapObject);
                });
            }
            if (name.startsWith("elevationAgents")) {
                loadObjectLayer(tiledMap, name, mapObject -> {
                    MapEntityFactory.createElevationAgent(box2DWorld.getWorld(), mapObject);
                });
            }
            if (name.startsWith("checkpoints")) {
                loadObjectLayer(tiledMap, name, mapObject -> {
                    MapEntityFactory.createCheckpoint(mapObject, () -> {
                    });
                });
            }
            if (name.startsWith("enemies")) {
                loadTileLayer(tiledMap, name, (i, j) -> EntityFactory.createEnemy(new Vector3(i, j, 1), 1, EntityType.goblin));//starts on one level of elevation higher to stabilise the shadow
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


}
