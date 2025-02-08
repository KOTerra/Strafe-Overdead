package com.strafergame.game.world;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.factories.EntityFactory;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.game.world.map.MapManager;
import com.strafergame.input.PlayerControl;
import com.strafergame.ui.HUD;

import java.util.Locale;

public class GameWorld implements Disposable {

    private TiledMap currentMap;

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
        //create systems
        entityEngine = EntityEngine.getInstance();
        entityEngine.initSystems(box2DWorld, rayHandler);

        mapManager = new MapManager(box2DWorld, rayHandler);

        SaveSystem.getCurrentSave().deserialize();
        //create player
        player = EntityFactory.createPlayer();

        //load map
        currentMap = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);    //TODO replace with get(from save path)
        mapManager.loadMap(currentMap);
    }

    public void triggerLoad() {
        //TODO load things back in existing entities and map also take care of old instances such as maps that are replaced, those should be disposed of
//load saved data

        SaveSystem.getPlayerSaveData().invalidate();
        SaveSystem.getCurrentSave().deserialize();
        SaveSystem.getPlayerSaveData().retrieve();
        SaveSystem.getPlayerSaveData().register();
        SaveSystem.getPlayerSaveData().loadOwner();

        //world and meta
    }

    public void update(float delta) {
        entityEngine.update(delta);
        debugUpdate();
    }

    @Override
    public void dispose() {
        entityEngine.dispose();
    }


    static void debugInfo() {
        HUD.debugInfoText = "FPS: " + Gdx.graphics.getFramesPerSecond() + '\n'
                + "Player State: " + ComponentMappers.entityType().get(player).entityState + '\n'
                + "Grounded: " + ClimbFallSystem.isGrounded(player) + '\n'

                + "Climbing: " + ComponentMappers.elevation().get(player).isClimbing + '\n'
                + "Player Elevation: " + ComponentMappers.elevation().get(player).elevation + '\n'
                + "Target Y: " + ComponentMappers.elevation().get(player).fallTargetY + '\n'
                + "Target Cell: " + ComponentMappers.elevation().get(player).fallTargetCell + '\n'
                + "Target Elevation: " + ComponentMappers.elevation().get(player).fallTargetElevation + '\n'


                + "x: " + Math.round(ComponentMappers.position().get(player).renderPos.x) + " y: " + Math.round(ComponentMappers.position().get(player).renderPos.y) + '\n'
                + PlayerControl.actionSequence.toString() + '\n'
                + PlayerControl.actionSequence.getSequenceKeycodes(10) + '\n'
        //+ PlayerControl.actionSequence.isInTimeframe(3,500);
        ;
        if (HUD.debugInfo != null) {
            HUD.debugInfo.setText(HUD.debugInfoText);
        }
    }


    void debugControls() {
        if (Strafer.inDebug) {


            if (Gdx.input.isKeyPressed(Keys.F7)) {
                Strafer.worldCamera.zoom += .02f;
            }
            if (Gdx.input.isKeyPressed(Keys.F9)) {
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

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_4)) {
                HealthComponent hc = ComponentMappers.health().get(player);
                hc.hitPoints -= 3;
                ComponentMappers.elevation().get(player).elevation = 0;
                ComponentMappers.position().get(player).elevation = 0;
                //a function somewhere to update both elevations and begin contacts of the body again

            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {
                HealthComponent hc = ComponentMappers.health().get(player);
                hc.hitPoints += 3;
                ComponentMappers.elevation().get(player).elevation = 1;
                ComponentMappers.position().get(player).elevation = 1;

            }
            if (Gdx.input.isKeyPressed(Keys.NUMPAD_6)) {
                HealthComponent hc = ComponentMappers.health().get(player);
                hc.hitPoints += 3;
                ComponentMappers.elevation().get(player).elevation = 2;
                ComponentMappers.position().get(player).elevation = 2;

            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
                Strafer.i18n = I18NBundle.createBundle(Gdx.files.internal("assets/i18n/ui/bundle"), new Locale("ro"), "utf-8");
            }
        }
    }

    void debugUpdate() {

        debugControls();
        debugInfo();
    }


    public Box2DWorld getBox2DWorld() {
        return box2DWorld;
    }

}
