package com.strafergame.game.world.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.factories.EntityFactory;
import com.strafergame.game.ecs.factories.MapEntityFactory;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;

public class MapManager {


    private Box2DWorld box2DWorld;
    private RayHandler rayHandler;


    public MapManager(Box2DWorld box2dworld, RayHandler rayHandler) {
        this.box2DWorld = box2dworld;
        this.rayHandler = rayHandler;
    }

    public void loadMap(TiledMap tiledMap) {
        addTestAssets(tiledMap);
    }

    private void addTestAssets(TiledMap tiledMapTest) {

        Strafer.worldCamera.setFocusOn(GameWorld.player);

        Strafer.tiledMapRenderer.setMap(tiledMapTest);


        loadTileLayer(tiledMapTest, "walls", (i, j) -> {
            //  Box2DFactory.createWall(box2DWorld.getWorld(), 1, 1, new Vector3(i, j, 0));
        });
        loadObjectLayer(tiledMapTest, "collisions", mapObject -> {
            Box2DFactory.createCollision(box2DWorld.getWorld(), mapObject);
            ///
        });

        loadObjectLayer(tiledMapTest, "checkpoints", mapObject -> {
            final float x = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("x") - .5f;
            final float y = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("y") - .5f;
            MapEntityFactory.createCheckpoint(() -> {
                // System.out.println("checkpoint reached");
            }, new Vector2(x, y));
        });

        loadTileLayer(tiledMapTest, "enemies", (i, j) -> EntityFactory.createEnemy(new Vector2(i, j), 1));

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
}
