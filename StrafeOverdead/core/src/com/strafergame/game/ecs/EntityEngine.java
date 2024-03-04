package com.strafergame.game.ecs;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.system.AnimationSystem;
import com.strafergame.game.ecs.system.CheckpointSystem;
import com.strafergame.game.ecs.system.MovementSystem;
import com.strafergame.game.ecs.system.camera.CameraSystem;
import com.strafergame.game.ecs.system.combat.CombatSystem;
import com.strafergame.game.ecs.system.combat.HealthSystem;
import com.strafergame.game.ecs.system.items.AttachmentSystem;
import com.strafergame.game.ecs.system.player.HudSystem;
import com.strafergame.game.ecs.system.player.PlayerControlSystem;
import com.strafergame.game.ecs.system.render.RenderingSystem;
import com.strafergame.game.ecs.system.save.AutoSaveSystem;
import com.strafergame.game.world.collision.Box2DWorld;

public class EntityEngine extends PooledEngine implements Disposable {

    private static EntityEngine instance;

    private boolean initialised = false;
    final Strafer game;
    private Box2DWorld box2dWorld;
    private RayHandler rayHandler;

    private AutoSaveSystem autoSaveSystem;
    private MovementSystem movementSystem;
    private HealthSystem healthSystem;
    private PlayerControlSystem playerControlSystem;

    private final RenderingSystem renderingSystem = new RenderingSystem();
    private final CheckpointSystem checkpointSystem = new CheckpointSystem();
    private final AnimationSystem animationSystem = new AnimationSystem();
    private final AttachmentSystem attachmentSystem = new AttachmentSystem();
    private final CombatSystem combatSystem = new CombatSystem();
    private final CameraSystem cameraSystem = new CameraSystem();
    private final HudSystem hudSystem = new HudSystem();

    public EntityEngine() {
        super();
        this.game = Strafer.getInstance();
    }

    public void initSystems(Box2DWorld box2dWorld, RayHandler rayHandler) {
        if (!initialised) {
            this.box2dWorld = box2dWorld;
            this.rayHandler = rayHandler;
            autoSaveSystem = new AutoSaveSystem(300);
            movementSystem = new MovementSystem(this.box2dWorld);
            healthSystem = new HealthSystem(box2dWorld);
            playerControlSystem = new PlayerControlSystem();

            // iterating systems
            addSystem(animationSystem);
            addSystem(movementSystem);
            addSystem(playerControlSystem);
            addSystem(healthSystem);
            addSystem(attachmentSystem);
            addSystem(combatSystem);
            addSystem(cameraSystem);
            addSystem(hudSystem);
            addSystem(checkpointSystem);
            addSystem(autoSaveSystem);
            addSystem(renderingSystem);
            initialised = true;
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }


    /**
     * Pauses or unpauses the processing of EntitySystems that are already added to the Entity Engine
     *
     * @param systems what systems' process state should be changed, Leave null if the default ones are to be used
     * @param pause   true if to pause, false if to unpause
     */
    public void pauseOnSystems(Array<EntitySystem> systems, boolean pause) {
        if (systems == null) {
            systems = new Array<>();
            systems.add(movementSystem, playerControlSystem, combatSystem, healthSystem);
            systems.add(animationSystem);
        }
        for (EntitySystem sys : systems) {
            if (this.getSystems().contains(sys, true)) {
                sys.setProcessing(!pause);
            }
        }
    }

    @Override
    public void dispose() {
        box2dWorld.dispose();
        rayHandler.dispose();
    }

    public Box2DWorld getBox2dWorld() {
        return box2dWorld;
    }

    public static EntityEngine getInstance() {
        if (instance == null) {
            instance = new EntityEngine();
        }
        return instance;
    }

    public boolean isInitialised() {
        return initialised;
    }
}
