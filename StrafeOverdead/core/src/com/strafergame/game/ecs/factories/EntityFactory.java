package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.strafergame.Strafer;
import com.strafergame.assets.AnimationProvider;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.*;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.GameWorld;
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
        posCmp.renderPos = playerSpawnLocation.cpy();
        player.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.gravity = true;
        elvCmp.elevation = 0;
        posCmp.elevation = 0;
        player.add(elvCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        movCmp.maxLinearSpeed = plyrCmp.baseSpeed;
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
        plyrCmp.sensor = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
        b2dCmp.body.setUserData(player);

        b2dCmp.body.setTransform(playerSpawnLocation, 0);

        player.add(entityEngine.createComponent(SteeringComponent.class).setOwner(player));

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
        posCmp.renderPos = location;
        dummy.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.gravity = true;
        elvCmp.elevation = 0;
        posCmp.elevation = 0;
        dummy.add(elvCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
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
        dctrCmp.detector = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        dummy.add(dctrCmp);

        b2dCmp.body.setTransform(location, 0);

        SteeringComponent steerCmp = entityEngine.createComponent(SteeringComponent.class);
        steerCmp.setOwner(dummy);
        SteeringComponent playerSteerCmp = ComponentMappers.steering().get(GameWorld.player);
        steerCmp.behavior = new Arrive<>(steerCmp, playerSteerCmp);
        dummy.add(steerCmp);

        return dummy;
    }


    public static void initPhysics(Entity e) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
        PositionComponent posCmp = ComponentMappers.position().get(e);
        SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

        posCmp.prevPos.x = -spriteCmp.width / 2;
        posCmp.prevPos.y = -spriteCmp.height / 2;

        Box2DFactory.createBody(b2dCmp, entityEngine.getBox2dWorld().getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
                posCmp.prevPos, BodyDef.BodyType.DynamicBody);
        Box2DFactory.addHurtboxToBody(entityEngine.getBox2dWorld().getWorld(), b2dCmp, spriteCmp.width, spriteCmp.height, 0,
                spriteCmp.height / 2);

        b2dCmp.body.setUserData(e);

        b2dCmp.initiatedPhysics = true;
    }

}
