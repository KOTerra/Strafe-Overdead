package com.strafergame.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.system.AnimationSystem;
import com.strafergame.game.ecs.system.CameraSystem;
import com.strafergame.game.ecs.system.MovementSystem;
import com.strafergame.game.ecs.system.player.PlayerControlSystem;
import com.strafergame.game.ecs.system.render.RenderingSystem;
import com.strafergame.game.entities.EntityType;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.graphics.AnimationProvider;

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
		addSystem(new MovementSystem(this.box2dWorld));
		addSystem(new PlayerControlSystem());
		addSystem(new CameraSystem());
		addSystem(new RenderingSystem(Strafer.spriteBatch));

	}

	public Entity createPlayer(final Vector2 playerSpawnLocation) {
		final Entity player = this.createEntity();
		PlayerComponent plyrCmp = this.createComponent(PlayerComponent.class);
		player.add(plyrCmp);

		EntityTypeComponent typeCmp = this.createComponent(EntityTypeComponent.class);
		typeCmp.entityType = EntityType.player;
		player.add(typeCmp);

		PositionComponent posCmp = this.createComponent(PositionComponent.class);
		posCmp.isHidden = false;
		posCmp.x = playerSpawnLocation.x;
		posCmp.y = playerSpawnLocation.y;
		player.add(posCmp);

		MovementComponent movCmp = this.createComponent(MovementComponent.class);
		movCmp.speed = plyrCmp.baseSpeed;
		player.add(movCmp);

		SpriteComponent spriteCmp = this.createComponent(SpriteComponent.class);
		player.add(spriteCmp);

		AnimationComponent aniCmp = this.createComponent(AnimationComponent.class);
		aniCmp.animation = AnimationProvider.getAnimation(player);
		player.add(aniCmp);

		Box2dComponent b2dCmp = this.createComponent(Box2dComponent.class);
		player.add(b2dCmp);
		this.addEntity(player);
		return player;
	}

	@Override
	public void dispose() {
	}

}
