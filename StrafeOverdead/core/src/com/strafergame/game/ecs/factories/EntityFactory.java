package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.strafergame.game.ecs.system.save.GdxPreferencesSerializer;
import com.strafergame.game.ecs.system.save.SaveAction;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

import java.io.IOException;


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

        AutoSaveComponent asvCmp = entityEngine.createComponent(AutoSaveComponent.class);
        asvCmp.saveAction = () -> {//make list of save entries add all with save actions type and key and where to apply them when loading?
//            GdxPreferencesSerializer.saveToPreferences("PLAYER_POSITION_COMPONENT", posCmp, PositionComponent.class);
            SaveSystem.getCurrentSave().register("PLAYER_POSITION_COMPONENT", posCmp, PositionComponent.class);
            SaveSystem.getCurrentSave().register("PLAYER_ELEVATION_COMPONENT", elvCmp, ElevationComponent.class);
            SaveSystem.getCurrentSave().serialize();

        };
        player.add(asvCmp);

        entityEngine.addEntity(player);
        initPhysics(player);
        plyrCmp.sensor = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
        b2dCmp.body.setUserData(player);

        b2dCmp.body.setTransform(playerSpawnLocation, 0);

        player.add(entityEngine.createComponent(SteeringComponent.class).setOwner(player));


        return player;
    }

    public static Entity createEnemy(final Vector3 location, float scale, EntityType type) {
        final Entity enemy = entityEngine.createEntity();
        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = type;
        typeCmp.entityState = EntityState.idle;
        enemy.add(typeCmp);
        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = type;
        enemy.add(camCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.isHidden = false;
        posCmp.renderPos = new Vector2(location.x, location.y);
        enemy.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.gravity = true;
        elvCmp.elevation = (int) location.z;
        posCmp.elevation = (int) location.z;
        enemy.add(elvCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        enemy.add(movCmp);

        HealthComponent hlthComponent = entityEngine.createComponent(HealthComponent.class);
        hlthComponent.hitPoints = 10;
        enemy.add(hlthComponent);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        enemy.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/goblin_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * scale * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * scale * Strafer.SCALE_FACTOR;

        AnimationComponent aniCmp = entityEngine.createComponent(AnimationComponent.class);
        aniCmp.animation = AnimationProvider.getAnimation(enemy);
        enemy.add(aniCmp);

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        enemy.add(b2dCmp);
        entityEngine.addEntity(enemy);

        initPhysics(enemy);
        DetectorComponent dctrCmp = entityEngine.createComponent(DetectorComponent.class);
        b2dCmp.body.setUserData(enemy);
        dctrCmp.detector = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        enemy.add(dctrCmp);

        b2dCmp.body.setTransform(posCmp.renderPos, 0);

        SteeringComponent steerCmp = entityEngine.createComponent(SteeringComponent.class);
        steerCmp.setOwner(enemy);
        SteeringComponent playerSteerCmp = ComponentMappers.steering().get(GameWorld.player);
        steerCmp.behavior = new Seek<>(steerCmp, playerSteerCmp);
        enemy.add(steerCmp);

        return enemy;
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
