package com.strafergame.game.ecs;

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
import com.strafergame.graphics.AnimationProvider;

import box2dLight.RayHandler;

public class EntityEngine extends PooledEngine implements Disposable {

	final Strafer game;
	private final Box2DWorld box2dWorld;
	private final RayHandler rayHandler;

	public EntityEngine(final Strafer game, final Box2DWorld box2dWorld, final RayHandler rayHandler) {
		super();
		this.game = game;
		this.box2dWorld = box2dWorld;
		this.rayHandler = rayHandler;

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

	public Entity createPlayer(int hp,final Vector2 playerSpawnLocation) {
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
		movCmp.dashDuration = 1f;
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
		hlthComponent.hitPoints = hp;
		player.add(hlthComponent);

		this.addEntity(player);
		initPhysics(player);
		plyrCmp.sensor = Box2DFactory.createSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
				FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
		plyrCmp.sensor.setUserData(player);

		b2dCmp.body.setTransform(playerSpawnLocation, 0);
		return player;
	}

	public Entity createEnemy(final Vector2 location, float scale) {
		final Entity dummy = this.createEntity();
		EntityTypeComponent typeCmp = this.createComponent(EntityTypeComponent.class);
		typeCmp.entityType = EntityType.dummy;
		typeCmp.entityState= EntityState.idle;
		dummy.add(typeCmp);
		CameraComponent camCmp = this.createComponent(CameraComponent.class);
		camCmp.type = EntityType.dummy;
		dummy.add(camCmp);

		PositionComponent posCmp = this.createComponent(PositionComponent.class);
		posCmp.isHidden = false;
		posCmp.x = location.x;
		posCmp.y = location.y;
		dummy.add(posCmp);

		MovementComponent movCmp = this.createComponent(MovementComponent.class);
		movCmp.speed = 0;
		dummy.add(movCmp);

		HealthComponent hlthComponent = this.createComponent(HealthComponent.class);
		hlthComponent.hitPoints = 10;
		dummy.add(hlthComponent);

		SpriteComponent spriteCmp = this.createComponent(SpriteComponent.class);
		dummy.add(spriteCmp);
		spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/dummy_static.png", Texture.class));
		spriteCmp.height = spriteCmp.sprite.getHeight() * scale * Strafer.SCALE_FACTOR;
		spriteCmp.width = spriteCmp.sprite.getWidth() * scale * Strafer.SCALE_FACTOR;

		Box2dComponent b2dCmp = this.createComponent(Box2dComponent.class);
		dummy.add(b2dCmp);
		this.addEntity(dummy);

		initPhysics(dummy);
		DetectorComponent dctrCmp = this.createComponent(DetectorComponent.class);
		b2dCmp.body.setUserData(dummy);
		dctrCmp.detector = Box2DFactory.createSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
				FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
		dummy.add(dctrCmp);

		b2dCmp.body.setTransform(location, 0);
		return dummy;
	}

	public Entity createHitboxDummy(final Vector2 location, int width, int height, final Entity owner) {
		final Entity dummy = this.createEntity();
		AttackComponent attckCmp = this.createComponent(AttackComponent.class);

		attckCmp.owner = owner;
		attckCmp.damagePerSecond = 10;
		attckCmp.doesKnockback = true;
		attckCmp.knockbackMagnitude = 5;
		Box2DFactory.createBodyWithHitbox(attckCmp, box2dWorld.getWorld(), width, height, 0, 0, location);
		dummy.add(attckCmp);
		this.addEntity(dummy);
		return dummy;
	}

	public Entity createItem(Entity owner,final Vector2 holdPos, int width, int height){
		Entity item=new Entity();
		ItemComponent itmCmp=this.createComponent(ItemComponent.class);
		itmCmp.owner=owner;
		itmCmp.holdPosition=holdPos;
		item.add(itmCmp);

		PositionComponent posCmp=this.createComponent(PositionComponent.class);
		item.add(posCmp);

		AttackComponent attckCmp = this.createComponent(AttackComponent.class);
		attckCmp.owner=owner;
		attckCmp.damagePerSecond=40;
		attckCmp.doesKnockback=true;
		attckCmp.knockbackMagnitude=5;
		Box2DFactory.createBodyWithHitbox(attckCmp, box2dWorld.getWorld(), width, height, 0, 0, holdPos);
		item.add(attckCmp);

		return item;
	}

	public Entity createCheckpoint(CheckpointAction action, final Vector2 location) {
		final Entity checkpoint = this.createEntity();
		CheckpointComponent chkCmp = this.createComponent(CheckpointComponent.class);
		chkCmp.action = action;
		checkpoint.add(chkCmp);

		PositionComponent posCmp = this.createComponent(PositionComponent.class);
		posCmp.renderX = location.x;
		posCmp.renderY = location.y;
		checkpoint.add(posCmp);

		CameraComponent camCmp = this.createComponent(CameraComponent.class);
		camCmp.type = EntityType.checkpoint;
		checkpoint.add(camCmp);

		Body body = Box2DFactory.createBody(box2dWorld.getWorld(), 1f, 1f, location, BodyType.StaticBody);
		body.setUserData(checkpoint);
		DetectorComponent dctrCmp = this.createComponent(DetectorComponent.class);
		dctrCmp.detector = Box2DFactory.createSensor(body, FilteredContactListener.DETECTOR_RADIUS,
				FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
		checkpoint.add(dctrCmp);
		this.addEntity(checkpoint);

		return checkpoint;
	}

	private void initPhysics(Entity e) {
		Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
		PositionComponent posCmp = ComponentMappers.position().get(e);
		SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

		posCmp.prevX = -spriteCmp.width / 2;
		posCmp.prevY = -spriteCmp.height / 2;

		Box2DFactory.createBody(b2dCmp, box2dWorld.getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
				new Vector2(posCmp.prevX, posCmp.prevY), BodyType.DynamicBody);
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
