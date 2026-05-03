package com.strafergame.game.world;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.factories.ArticyEntityFactory;
import com.strafergame.game.ecs.factories.EntityFactory;
import com.strafergame.game.ecs.factories.EntityRegistry;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.game.world.map.MapManager;
import com.strafergame.graphics.ColorPallete;
import com.strafergame.input.PlayerControl;
import com.strafergame.ui.HUD;
import com.articy.runtime.core.ArticyRuntime;
import com.articy.runtime.core.ArticyFlowPlayer;
import com.articy.runtime.model.Branch;
import com.articy.runtime.core.IArticyFlowPlayerCallbacks;
import com.articy.runtime.logic.ArticyVariableManager;
import com.articy.runtime.model.FlowObject;
import com.strafergame.game.story.ArticyScriptMethodProvider;
import com.strafergame.game.ecs.system.save.data.ArticySaveData;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GameWorld implements Disposable {

    private TiledMap currentMap;
    public static final float FIXED_TIME_STEP = 1 / 90f;

    private final Box2DWorld box2DWorld = new Box2DWorld();
    private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());


    private final MapManager mapManager;
    private final EntityEngine entityEngine;

    public static Entity player;
    private static boolean spawnTriggered = false;
    private static boolean cameraTriggered = false;
    private List<Branch> currentBranches;

    public GameWorld() {
        entityEngine = EntityEngine.getInstance();
        entityEngine.initSystems(box2DWorld, rayHandler);

        initLight();
        initArticy();

        mapManager = new MapManager(box2DWorld, rayHandler);
        SaveSystem.getCurrentSave().deserialize();
        player = EntityRegistry.create(EntityType.player, null, null);
        currentMap = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);
        mapManager.loadMap(currentMap);
    }

    private void initArticy() {
        try {
            ArticyScriptMethodProvider methodProvider = new ArticyScriptMethodProvider();

            // Try different possible paths for the export directory
            String[] possiblePaths = {
                "assets/articy/export/Strafe-Overdead-test"
                    ,
                "../assets/articy/export",
                "../../assets/articy/export"
            };

            String exportDir = null;
            for (String path : possiblePaths) {
                java.io.File dir = new java.io.File(path);
                if (dir.exists() && dir.isDirectory()) {
                    exportDir = path;
                    break;
                }
            }

            if (exportDir == null) {
                Gdx.app.error("Articy", "Could not find Articy-Test-Project-json in any expected location.");
                return;
            }

            System.out.println("Articy: Loading from " + new java.io.File(exportDir).getAbsolutePath());
            ArticyRuntime.initialize(exportDir, methodProvider);

            ArticyFlowPlayer flowPlayer = new ArticyFlowPlayer(
                ArticyRuntime.getDatabase(), 
                ArticyRuntime.getVariableManager(), 
                ArticyRuntime.getEngine(), 
                methodProvider,
                new IArticyFlowPlayerCallbacks() {
                    @Override
                    public void onBranchesUpdated(List<Branch> branches) {
                        currentBranches = branches;
                        System.out.println("Articy: Branches updated, count: " + branches.size() + " [Press 0, 1, etc. to choose]");
                        for (int i = 0; i < branches.size(); i++) {
                            Branch b = branches.get(i);
                            String text = "";
                            if (b.getTargetNode() instanceof com.articy.runtime.model.DialogueFragment) {
                                com.articy.runtime.model.DialogueFragment df = (com.articy.runtime.model.DialogueFragment) b.getTargetNode();
                                text = df.getMenuText() != null && !df.getMenuText().isEmpty() ? df.getMenuText() : df.getText();
                            }
                            System.out.println("  [" + i + "]: " + text + " (Target: " + b.getTargetNode().getTechnicalName() + ")");
                        }
                    }

                    @Override
                    public void onFlowPlayerPaused(FlowObject object) {
                        System.out.println("Articy: Flow player paused on: " + object.getTechnicalName() + " (Text: " + (object instanceof com.articy.runtime.model.DialogueFragment ? ((com.articy.runtime.model.DialogueFragment)object).getText() : "") + ")");
                    }
                }
            );
            ArticyRuntime.setFlowPlayer(flowPlayer);
            System.out.println("Articy Runtime initialized successfully.");
        } catch (IOException e) {
            Gdx.app.error("Articy", "Failed to initialize Articy Runtime", e);
        }
    }
    private void initLight() {

        //rayHandler.setAmbientLight(0.4f);

        rayHandler.setAmbientLight(ColorPallete.AMBIENT_NIGHT_LIGHT_COLOR);

        rayHandler.setBlur(true);
        rayHandler.setBlurNum(3);

    }

    public void triggerLoad() {
        SaveSystem.getPlayerSaveData().invalidate();
        SaveSystem.getCurrentSave().deserialize();
        SaveSystem.getArticySaveData().retrieve();
        SaveSystem.getPlayerSaveData().retrieve();
        SaveSystem.getPlayerSaveData().register();
        SaveSystem.getPlayerSaveData().loadOwner();
    }

    public void update(float delta) {
        entityEngine.update(delta);
        debugUpdate();

        // Articy Trigger Check
        if (player != null) {
            com.strafergame.game.ecs.component.physics.PositionComponent posCmp = ComponentMappers.position().get(player);
            if (posCmp != null) {
                // Step 1: Spawn NPC at (10, 10)
                if (!spawnTriggered) {
                    float targetX = 10f;
                    float targetY = 10f;
                    float distSq = (posCmp.renderPos.x - targetX) * (posCmp.renderPos.x - targetX) +
                                   (posCmp.renderPos.y - targetY) * (posCmp.renderPos.y - targetY);
                    if (distSq < 4.0f) {
                        spawnTriggered = true;
                        if (ArticyRuntime.getFlowPlayer() != null) {
                            System.out.println("Articy TRIGGER: Spawning NPC!");
                            ArticyRuntime.getFlowPlayer().startOn(0x0100000000000011L); // Zone_Spawn_NPC
                        }
                    }
                }
                
                // Step 2: Camera + Dialogue at (20, 10)
                if (spawnTriggered && !cameraTriggered) {
                    float targetX = 20f;
                    float targetY = 10f;
                    float distSq = (posCmp.renderPos.x - targetX) * (posCmp.renderPos.x - targetX) +
                                   (posCmp.renderPos.y - targetY) * (posCmp.renderPos.y - targetY);
                    if (distSq < 4.0f) {
                        cameraTriggered = true;
                        if (ArticyRuntime.getFlowPlayer() != null) {
                            System.out.println("Articy TRIGGER: Camera + Dialogue!");
                            ArticyRuntime.getFlowPlayer().startOn(0x010000000000000AL); // Zone_Trigger_Camera
                        }
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        entityEngine.dispose();
    }

    static void debugInfo() {
        HUD.debugInfoText = "FPS: " + Gdx.graphics.getFramesPerSecond() + '\n'
                + (PlayerControl.USING_CONTROLLER ? "C" : "K") + '\n'
                + "Player State: " + ComponentMappers.entityType().get(player).entityState + "_" + ComponentMappers.entityType().get(player).entitySubState + '\n'
                + "Player Elevation: " + ComponentMappers.elevation().get(player).elevation + '\n'
                + "Target Y: " + ComponentMappers.elevation().get(player).fallTargetY + '\n'
                + "Target Cell: " + ComponentMappers.elevation().get(player).fallTargetCell + '\n'
                + "Target Elevation: " + ComponentMappers.elevation().get(player).fallTargetElevation + '\n'
                + "xr: " + Math.round(ComponentMappers.position().get(player).renderPos.x) + " yr: " + Math.round(ComponentMappers.position().get(player).renderPos.y) + '\n'
                + "x: " + Math.round(ComponentMappers.position().get(player).renderPos.x * 100) / 100f + " y: " + Math.round(ComponentMappers.position().get(player).renderPos.y * 100) / 100f + '\n'
                + "Spawn Triggered: " + spawnTriggered + '\n'
                + "Camera Triggered: " + cameraTriggered + '\n'
//                + PlayerControl.actionSequence.toString() + '\n'
//                + PlayerControl.actionSequence.getSequenceKeycodes(10) + '\n'
//                + PlayerControl.actionSequence.isInTimeframe(3,500);
        ;
        if (HUD.debugInfo != null) {
            HUD.debugInfo.setText(Strafer.inDebug ? HUD.debugInfoText : "");
        }
    }

    void debugControls() {
        if (Gdx.input.isKeyJustPressed(Keys.F12)) {
            Strafer.inDebug = !Strafer.inDebug;
        }
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

            if (Gdx.input.isKeyJustPressed(Keys.NUMPAD_6)) {
                System.out.println("MANUAL Spawn TRIGGER!");
                if (ArticyRuntime.getFlowPlayer() != null) {
                    ArticyRuntime.getFlowPlayer().startOn(0x0100000000000011L); 
                }
            }

            if (Gdx.input.isKeyJustPressed(Keys.NUMPAD_7)) {
                System.out.println("MANUAL Camera TRIGGER!");
                if (ArticyRuntime.getFlowPlayer() != null) {
                    ArticyRuntime.getFlowPlayer().startOn(0x010000000000000AL); 
                }
            }

            if (Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
                Strafer.i18n = I18NBundle.createBundle(Gdx.files.internal("assets/i18n/ui/bundle"), new Locale("ro"), "utf-8");
            }
            if (Gdx.input.isKeyPressed(Keys.K)) {
                EntityTypeComponent t = ComponentMappers.entityType().get(player);
                ComponentMappers.health().get(player).hitPoints = -1;
                t.entityState = EntityState.death;
            }
            if (Gdx.input.isKeyPressed(Keys.L)) {
                ComponentMappers.health().get(player).init(ComponentMappers.stats().get(player).maxHealth);
            }

            // Articy Branch Selection
            if (currentBranches != null && !currentBranches.isEmpty()) {
                int index = -1;
                if (Gdx.input.isKeyJustPressed(Keys.NUM_0) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_0)) index = 0;
                if (Gdx.input.isKeyJustPressed(Keys.NUM_1) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_1)) index = 1;
                if (Gdx.input.isKeyJustPressed(Keys.NUM_2) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_2)) index = 2;

                if (index != -1 && currentBranches.size() > index) {
                    Branch selected = currentBranches.get(index);
                    currentBranches = null; // Clear to prevent double trigger
                    ArticyRuntime.getFlowPlayer().advance(selected);
                }
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