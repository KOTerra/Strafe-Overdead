package com.strafergame.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.system.AnimationSystem;
import com.strafergame.game.ecs.system.CameraSystem;
import com.strafergame.game.ecs.system.MovementSystem;
import com.strafergame.game.ecs.system.combat.CombatSystem;
import com.strafergame.game.ecs.system.combat.HealthSystem;
import com.strafergame.game.ecs.system.player.PlayerControlSystem;
import com.strafergame.game.ecs.system.render.RenderingSystem;
import com.strafergame.game.entities.EntityType;
import com.strafergame.game.world.collision.Box2DFactory;
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
		addSystem(new HealthSystem(box2dWorld));
		addSystem(new CombatSystem());
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
		movCmp.dashForce = plyrCmp.dashForce;

		player.add(movCmp);

		SpriteComponent spriteCmp = this.createComponent(SpriteComponent.class);
		player.add(spriteCmp);
		spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/player_static.png", Texture.class));
		spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
		spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;

		AnimationComponent aniCmp = this.createComponent(AnimationComponent.class);
		aniCmp.animation = AnimationProvider.getAnimation(player);
		player.add(aniCmp);

		Box2dComponent b2dCmp = this.createComponent(Box2dComponent.class);
		player.add(b2dCmp);

		HealthComponent hlthComponent = this.createComponent(HealthComponent.class);
		hlthComponent.hitPoints = 10;
		player.add(hlthComponent);

		this.addEntity(player);
		initPhysics(player);
		return player;
	}

	public Entity createDummy(final Vector2 location, float scale) {
		final Entity dummy = this.createEntity();
		EntityTypeComponent typeCmp = this.createComponent(EntityTypeComponent.class);
		typeCmp.entityType = EntityType.dummy;
		dummy.add(typeCmp);

		PositionComponent posCmp = this.createComponent(PositionComponent.class);
		posCmp.isHidden = false;
		posCmp.x = location.x;
		posCmp.y = location.y;
		dummy.add(posCmp);

		MovementComponent movCmp = this.createComponent(MovementComponent.class);
		movCmp.speed = 0;
		dummy.add(movCmp);

		SpriteComponent spriteCmp = this.createComponent(SpriteComponent.class);
		dummy.add(spriteCmp);
		spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/dummy_static.png", Texture.class));
		spriteCmp.height = spriteCmp.sprite.getHeight() * scale * Strafer.SCALE_FACTOR;
		spriteCmp.width = spriteCmp.sprite.getWidth() * scale * Strafer.SCALE_FACTOR;

		Box2dComponent b2dCmp = this.createComponent(Box2dComponent.class);
		dummy.add(b2dCmp);
		this.addEntity(dummy);

		initPhysics(dummy);
		return dummy;
	}

	public Entity createHitboxDummy(final Vector2 location) {
		final Entity dummy = this.createEntity();
		AttackComponent attckCmp = this.createComponent(AttackComponent.class);

		attckCmp.damagePerSecond = 1;
		attckCmp.doesKnockback = true;
		attckCmp.knockbackMagnitude = 5;
		Box2DFactory.createBodyWithHitbox(attckCmp, box2dWorld.getWorld(), 1, 1, 0, 0, location);
		dummy.add(attckCmp);

		return dummy;
	}

	private void initPhysics(Entity e) {
		Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
		PositionComponent posCmp = ComponentMappers.position().get(e);
		SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

		posCmp.prevX = -spriteCmp.width / 2;
		posCmp.prevY = -spriteCmp.height / 2;

		Box2DFactory.createBody(b2dCmp, box2dWorld.getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
				new Vector3(posCmp.prevX, posCmp.prevY, 0), BodyType.DynamicBody);
		Box2DFactory.addHurtboxToBody(box2dWorld.getWorld(), b2dCmp, spriteCmp.width, spriteCmp.height, 0,
				spriteCmp.height / 2);
		b2dCmp.initiatedPhysics = true;
	}

	@Override
	public void dispose() {
		box2dWorld.dispose();
		rayHandler.dispose();
	}

}
