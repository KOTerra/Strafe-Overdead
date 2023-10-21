package com.strafergame.game.ecs;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.AnimationSystem;
import com.strafergame.game.ecs.system.CheckpointSystem;
import com.strafergame.game.ecs.system.MovementSystem;
import com.strafergame.game.ecs.system.camera.CameraSystem;
import com.strafergame.game.ecs.system.combat.CombatSystem;
import com.strafergame.game.ecs.system.combat.HealthSystem;
import com.strafergame.game.ecs.system.items.ItemHoldSystem;
import com.strafergame.game.ecs.system.player.HudSystem;
import com.strafergame.game.ecs.system.player.PlayerControlSystem;
import com.strafergame.game.ecs.system.render.RenderingSystem;
import com.strafergame.game.ecs.system.save.AutoSaveSystem;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.game.world.collision.FilteredContactListener;
import com.strafergame.assets.AnimationProvider;

public class EntityEngine extends PooledEngine implements Disposable {

    final Strafer game;
    private final Box2DWorld box2dWorld;
    private final RayHandler rayHandler;

    public EntityEngine(final Strafer game, final Box2DWorld box2dWorld, final RayHandler rayHandler) {
        super();
        this.game = game;
        this.box2dWorld = box2dWorld;
        this.rayHandler = rayHandler;
        EntityFactory.entityEngine = this;

        // iterating systems
        addSystem(new AnimationSystem());
        addSystem(new MovementSystem(this.box2dWorld));
        addSystem(new PlayerControlSystem(this.game));
        addSystem(new HealthSystem(box2dWorld));
        addSystem(new ItemHoldSystem());
        addSystem(new CombatSystem());
        addSystem(new CameraSystem());
        addSystem(new HudSystem());
        addSystem(new CheckpointSystem());
        addSystem(new AutoSaveSystem(300));
        addSystem(new RenderingSystem(Strafer.spriteBatch));

        // addSystem(new ProximityTestSystem());

    }


    @Override
    public void dispose() {
        box2dWorld.dispose();
        rayHandler.dispose();
    }

    public Box2DWorld getBox2dWorld() {
        return box2dWorld;
    }
}
