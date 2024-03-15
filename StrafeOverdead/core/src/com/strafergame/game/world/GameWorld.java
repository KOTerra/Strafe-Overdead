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
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.factories.EntityFactory;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.factories.MapEntityFactory;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.game.world.map.MapManager;
import com.strafergame.game.world.map.ObjectLayerLoadAction;
import com.strafergame.game.world.map.TileLayerLoadAction;

import java.util.Locale;

public class GameWorld implements Disposable {

    private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);
    private Vector2 playerSpawn = new Vector2(4, 2);
    private int playerInitialHealth = 100;

    /**
     * the physics engine updates 90 times each second no matter the framerate so it can use kg/m/s
     */
    public static final float FIXED_TIME_STEP = 1 / 90f;

    /**
     *
     */
    private final Box2DWorld box2DWorld = new Box2DWorld();
    private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());

    private final MapManager mapManager;
    private final EntityEngine entityEngine;

    public static Entity player;


    public GameWorld() {
        entityEngine = EntityEngine.getInstance();
        entityEngine.initSystems(box2DWorld, rayHandler);
        mapManager = new MapManager(box2DWorld, rayHandler);
        player = EntityFactory.createPlayer(playerInitialHealth, playerSpawn);

        mapManager.loadMap(tiledMapTest);
    }

    public void update(float delta) {
        entityEngine.update(delta);
        debugUpdate();
    }

    @Override
    public void dispose() {
        entityEngine.dispose();
    }


    public void reset() {
        for (Entity e : entityEngine.getEntities()) {
            if (e != player) {
                entityEngine.removeEntity(e);
            }
        }
        mapManager.loadMap(tiledMapTest);

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
                this.reset();
            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_4)) {
                HealthComponent hc = ComponentMappers.health().get(player);
                hc.hitPoints -= 3;
            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {
                HealthComponent hc = ComponentMappers.health().get(player);
                hc.hitPoints += 3;

            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
                Strafer.i18n = I18NBundle.createBundle(Gdx.files.internal("assets/i18n/ui/bundle"), new Locale("ro"), "utf-8");
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
