package com.strafergame.game.ecs;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.system.AnimationSystem;
import com.strafergame.game.ecs.system.CameraSystem;
import com.strafergame.game.ecs.system.MovementSystem;
import com.strafergame.game.ecs.system.player.PlayerMovementSystem;
import com.strafergame.game.ecs.system.render.RenderingSystem;
import com.strafergame.game.world.collision.Box2DWorld;

import box2dLight.RayHandler;

public class EntityEngine extends PooledEngine implements Disposable {

	private final Box2DWorld box2dWorld;
	private final RayHandler rayHandler;

	public EntityEngine(final Box2DWorld box2dWorld, final RayHandler rayHandler) {
		super();
		this.box2dWorld = box2dWorld;
		this.rayHandler = rayHandler;

		// iterating systems
		addSystem(new AnimationSystem());
		// addSystem(new MovementSystem(box2dWorld));
		addSystem(new PlayerMovementSystem());
		addSystem(new CameraSystem());
		addSystem(new RenderingSystem(Strafer.spriteBatch));

	}

	@Override
	public void dispose() {
	}

}
