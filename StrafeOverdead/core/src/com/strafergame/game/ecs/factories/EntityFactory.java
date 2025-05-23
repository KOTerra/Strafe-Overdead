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
import com.strafergame.game.ecs.component.world.ShadowComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.data.PlayerSaveData;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;


public abstract class EntityFactory {
    private static final EntityEngine entityEngine = EntityEngine.getInstance();

    public static Entity createPlayer() {
        final Entity player = entityEngine.createEntity();


        PlayerSaveData playerSaveData = SaveSystem.getPlayerSaveData();

        AutoSaveComponent asvCmp = entityEngine.createComponent(AutoSaveComponent.class);
        asvCmp.saveAction = () -> {
            playerSaveData.register();
            SaveSystem.getCurrentSave().serialize();
        };
        player.add(asvCmp);

        playerSaveData.setPlayer(player);
        playerSaveData.retrieve();
        playerSaveData.loadOwner();


        //deserialized
        StatsComponent statsCmp = playerSaveData.getStatsCmp();
        PositionComponent posCmp = playerSaveData.getPosCmp();
        ElevationComponent elvCmp = playerSaveData.getElvCmp();
        HealthComponent hlthCmp = playerSaveData.getHealthCmp();
        //deserialized


        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.player;
        player.add(typeCmp);

        PlayerComponent plyrCmp = entityEngine.createComponent(PlayerComponent.class);
        player.add(plyrCmp);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        player.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/player_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;

        AnimationComponent aniCmp = entityEngine.createComponent(AnimationComponent.class);
        aniCmp.animation = AnimationProvider.getAnimation(player);
        player.add(aniCmp);

        ShadowComponent shdCmp = entityEngine.createComponent(ShadowComponent.class);
        shdCmp.radius = aniCmp.animation.getKeyFrame(0).getWidth() * .4f;
        player.add(shdCmp);


        //dependant on serialization
        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        movCmp.maxLinearSpeed = statsCmp.baseSpeed;
        movCmp.dashDuration = statsCmp.dashDuration;
        movCmp.dashForce = statsCmp.dashForce;
        player.add(movCmp);


        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        player.add(b2dCmp);
        initPhysics(player);
        plyrCmp.sensor = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
        b2dCmp.body.setUserData(player);
        b2dCmp.body.setTransform(posCmp.renderPos, 0);

        player.add(entityEngine.createComponent(SteeringComponent.class).setOwner(player));//the steering

        entityEngine.addEntity(player);
        return player;
    }

    public static Entity createEnemy(final Vector3 location, float scale, EntityType type) {//use Object decorator pattern with this
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
        elvCmp.fallTargetY = ClimbFallSystem.TARGET_NOT_CALCULATED;
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

        ShadowComponent shdCmp = entityEngine.createComponent(ShadowComponent.class);
        shdCmp.radius = spriteCmp.width * 10f;
        enemy.add(shdCmp);

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        enemy.add(b2dCmp);
        entityEngine.addEntity(enemy);

        initPhysics(enemy);
        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        b2dCmp.body.setUserData(enemy);
        dtctrCmp.detector = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        enemy.add(dtctrCmp);

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
