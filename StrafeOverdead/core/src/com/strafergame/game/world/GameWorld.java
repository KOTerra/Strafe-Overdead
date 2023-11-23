package com.strafergame.game.world;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.EntityFactory;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.game.world.map.ObjectLayerLoadAction;
import com.strafergame.game.world.map.TileLayerLoadAction;

import java.util.Locale;

public class GameWorld implements Disposable {

    private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);

    public static final float FIXED_TIME_STEP = 1 / 90f;

    /**
     *
     */
    private final Box2DWorld box2DWorld = new Box2DWorld();
    private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());
    private final EntityEngine entityEngine;

    public static Entity player;

    private Vector2 playerSpawn = new Vector2(4, 2);
    private int playerInitialHealth = 100;

    public GameWorld() {
        entityEngine = new EntityEngine(box2DWorld, rayHandler);
        player = EntityFactory.createPlayer(playerInitialHealth, playerSpawn);

        addTestAssets();
    }

    public void update(float delta) {
        entityEngine.update(delta);
        debugUpdate();
    }

    @Override
    public void dispose() {
        entityEngine.dispose();
    }

    void addTestAssets() {

        Strafer.worldCamera.setFocusOn(player);

        Strafer.tiledMapRenderer.setMap(tiledMapTest);


        loadTileLayer(tiledMapTest, "walls", new TileLayerLoadAction() {
            @Override
            public void execute(int i, int j) {
                //  Box2DFactory.createWall(box2DWorld.getWorld(), 1, 1, new Vector3(i, j, 0));
            }
        });
        loadObjectLayer(tiledMapTest, "collisions", new ObjectLayerLoadAction() {
            @Override
            public void execute(MapObject mapObject) {
                Box2DFactory.createCollision(box2DWorld.getWorld(), mapObject);
            }
        });

        loadObjectLayer(tiledMapTest, "checkpoints", new ObjectLayerLoadAction() {

            @Override
            public void execute(MapObject mapObject) {
                final float x = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("x") - .5f;
                final float y = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("y") - .5f;
                EntityFactory.createCheckpoint(new CheckpointAction() {

                    @Override
                    public void execute() {
                        // System.out.println("checkpoint reached");
                    }
                }, new Vector2(x, y));
            }
        });

        loadTileLayer(tiledMapTest, "enemies", new TileLayerLoadAction() {
            @Override
            public void execute(int i, int j) {
                EntityFactory.createEnemy(new Vector2(i, j), 1);
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


    public void reset() {
        for (Entity e : entityEngine.getEntities()) {
            if (e != player) {
                entityEngine.removeEntity(e);
            }
        }
        addTestAssets();

        HealthComponent hlthCmp = ComponentMappers.health().get(player);
        hlthCmp.hitPoints = playerInitialHealth;
        EntityTypeComponent ettCmp = ComponentMappers.entityType().get(player);
        ettCmp.entityState = EntityState.idle;
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(player);
        b2dCmp.body.setTransform(playerSpawn, 0);
    }

    void debugControls() {
        if (Strafer.inDebug) {

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
                Strafer.worldCamera.zoom += .02f;
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
                Strafer.worldCamera.zoom -= .02f;
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_0)) {
                Strafer.worldCamera.removeFocus();
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_1)) {
                Strafer.worldCamera.setFocusOn(player);
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_2)) {
                // game.setScreen(Strafer.gameOverScreen);
                this.reset();
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {
                entityEngine.pauseSystems(null, true);
            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_6)) {
                entityEngine.pauseSystems(null, false);
            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
                Strafer.i18n = I18NBundle.createBundle(Gdx.files.internal("assets/i18n/ui/bundle"), new Locale("ro"),
                        "utf-8");
            }
        }
    }

    void debugUpdate() {

        debugControls();
    }


    public Box2DWorld getBox2DWorld() {
        return box2DWorld;
    }

}
