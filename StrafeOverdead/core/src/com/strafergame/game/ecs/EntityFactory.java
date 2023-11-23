package com.strafergame.game.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.strafergame.Strafer;
import com.strafergame.assets.AnimationProvider;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

public abstract class EntityFactory {
    private static final EntityEngine entityEngine = EntityEngine.getInstance();

    public static Entity createPlayer(int hp, final Vector2 playerSpawnLocation) {
        final Entity player = entityEngine.createEntity();
        PlayerComponent plyrCmp = entityEngine.createComponent(PlayerComponent.class);
        player.add(plyrCmp);

        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.player;
        player.add(typeCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.isHidden = false;
        posCmp.renderX = playerSpawnLocation.x;
        posCmp.renderY = playerSpawnLocation.y;
        player.add(posCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        movCmp.speed = plyrCmp.baseSpeed;
        movCmp.dashDuration = 1f;
        movCmp.dashForce = plyrCmp.dashForce;

        player.add(movCmp);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        player.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/player_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;

        AnimationComponent aniCmp = entityEngine.createComponent(AnimationComponent.class);
        aniCmp.animation = AnimationProvider.getAnimation(player);
        player.add(aniCmp);

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        player.add(b2dCmp);

        HealthComponent hlthComponent = entityEngine.createComponent(HealthComponent.class);
        hlthComponent.hitPoints = hp;
        player.add(hlthComponent);

        entityEngine.addEntity(player);
        initPhysics(player);
        plyrCmp.sensor = Box2DFactory.createSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
        plyrCmp.sensor.setUserData(player);

        b2dCmp.body.setTransform(playerSpawnLocation, 0);
        return player;
    }

    public static Entity createEnemy(final Vector2 location, float scale) {
        final Entity dummy = entityEngine.createEntity();
        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.dummy;
        typeCmp.entityState = EntityState.idle;
        dummy.add(typeCmp);
        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = EntityType.dummy;
        dummy.add(camCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.isHidden = false;
        posCmp.renderX = location.x;
        posCmp.renderY = location.y;
        dummy.add(posCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        movCmp.speed = 0;
        dummy.add(movCmp);

        HealthComponent hlthComponent = entityEngine.createComponent(HealthComponent.class);
        hlthComponent.hitPoints = 10;
        dummy.add(hlthComponent);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        dummy.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/dummy_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * scale * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * scale * Strafer.SCALE_FACTOR;

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        dummy.add(b2dCmp);
        entityEngine.addEntity(dummy);

        initPhysics(dummy);
        DetectorComponent dctrCmp = entityEngine.createComponent(DetectorComponent.class);
        b2dCmp.body.setUserData(dummy);
        dctrCmp.detector = Box2DFactory.createSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        dummy.add(dctrCmp);

        b2dCmp.body.setTransform(location, 0);
        return dummy;
    }

    public static Entity createHitboxDummy(final Vector2 location, int width, int height, final Entity owner) {
        final Entity dummy = entityEngine.createEntity();
        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);

        attckCmp.owner = owner;
        attckCmp.damagePerSecond = 10;
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 5;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, 0, 0, location);
        dummy.add(attckCmp);
        entityEngine.addEntity(dummy);
        return dummy;
    }

    public static Entity createItem(Entity owner, final Vector2 holdPos, int width, int height) {
        Entity item = new Entity();
        ItemComponent itmCmp = entityEngine.createComponent(ItemComponent.class);
        itmCmp.owner = owner;
        itmCmp.holdPosition = holdPos;
        item.add(itmCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        item.add(posCmp);

        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);
        attckCmp.owner = owner;
        attckCmp.damagePerSecond = 40;
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 5;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, 0, 0, holdPos);
        item.add(attckCmp);

        return item;
    }

    public static Entity createCheckpoint(CheckpointAction action, final Vector2 location) {
        final Entity checkpoint = entityEngine.createEntity();
        CheckpointComponent chkCmp = entityEngine.createComponent(CheckpointComponent.class);
        chkCmp.action = action;
        checkpoint.add(chkCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.renderX = location.x;
        posCmp.renderY = location.y;
        checkpoint.add(posCmp);

        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = EntityType.checkpoint;
        checkpoint.add(camCmp);

        Body body = Box2DFactory.createBody(entityEngine.getBox2dWorld().getWorld(), 1f, 1f, location, BodyDef.BodyType.StaticBody);
        body.setUserData(checkpoint);
        DetectorComponent dctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dctrCmp.detector = Box2DFactory.createSensor(body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        checkpoint.add(dctrCmp);
        entityEngine.addEntity(checkpoint);

        return checkpoint;
    }

    private static void initPhysics(Entity e) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
        PositionComponent posCmp = ComponentMappers.position().get(e);
        SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

        posCmp.prevX = -spriteCmp.width / 2;
        posCmp.prevY = -spriteCmp.height / 2;

        Box2DFactory.createBody(b2dCmp, entityEngine.getBox2dWorld().getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
                new Vector2(posCmp.prevX, posCmp.prevY), BodyDef.BodyType.DynamicBody);
        Box2DFactory.addHurtboxToBody(entityEngine.getBox2dWorld().getWorld(), b2dCmp, spriteCmp.width, spriteCmp.height, 0,
                spriteCmp.height / 2);
        b2dCmp.initiatedPhysics = true;
    }

}
