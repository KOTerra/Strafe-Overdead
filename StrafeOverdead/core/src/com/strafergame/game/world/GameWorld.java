package com.strafergame.game.world;

import java.util.Locale;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;

import box2dLight.RayHandler;
import com.strafergame.game.world.map.LayerLoadAction;

public class GameWorld implements Disposable {

    private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);

    public static final float FIXED_TIME_STEP = 1 / 90f;

    Strafer game;

    /**
     *
     */

    private final Box2DWorld box2DWorld = new Box2DWorld();
    private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());
    private final EntityEngine entityEngine;

    public static Entity player;

    public GameWorld(Strafer game) {
        this.game = game;
        entityEngine = new EntityEngine(game, box2DWorld, rayHandler);
        player = entityEngine.createPlayer(new Vector2(4, 2));

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

        //dummy = entityEngine.createEnemy(new Vector2(40, 60), 3);
      //  dummy2 = entityEngine.createEnemy(new Vector2(30, 20), 1);
        //entityEngine.createHitboxDummy(new Vector2(15, 5), 1, 8, null);


        Strafer.worldCamera.setFocusOn(player);

        Strafer.tiledMapRenderer.setMap(tiledMapTest);


        loadLayer(tiledMapTest, "walls", new LayerLoadAction() {
            @Override
            public void execute(int i, int j) {
                Box2DFactory.createWall(box2DWorld.getWorld(), 1, 1, new Vector3(i, j, 0));
            }
        });

        loadLayer(tiledMapTest, "checkpoint", new LayerLoadAction() {
            @Override
            public void execute(int i, int j) {
                entityEngine.createCheckpoint(new CheckpointAction() {

                    @Override
                    public void execute() {
                        // System.out.println("checkpoint reached");
                    }
                }, new Vector2(i, j));
            }
        });

        loadLayer(tiledMapTest, "enemies", new LayerLoadAction() {
            @Override
            public void execute(int i, int j) {
                entityEngine.createEnemy(new Vector2(i, j), 1);
            }
        });

    }

    private void loadLayer(TiledMap map, String layerName, LayerLoadAction lla) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        for (int i = 1; i <= layer.getWidth(); i++) {
            for (int j = 1; j <= layer.getHeight(); j++) {
                if (layer.getCell(i, j) != null) {
                    lla.execute(i,j);
                }
            }
        }
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

            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {

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
