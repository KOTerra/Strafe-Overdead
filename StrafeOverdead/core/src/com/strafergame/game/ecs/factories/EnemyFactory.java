package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.Strafer;
import com.strafergame.assets.AnimationProvider;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.ai.BehaviorTreeComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.ShadowComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

public class EnemyFactory implements EntityCreator {
    private final EntityType entityType;

    public EnemyFactory(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public Entity create(Vector3 position, MapObject mapObject) {
        float scale = 1f;
        if (mapObject != null) {
            scale = mapObject.getProperties().get("scale", 1f, Float.class);
        }

        EntityEngine entityEngine = EntityEngine.getInstance();
        final Entity enemy = entityEngine.createEntity();

        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeStr(enemy, typeCmp);

        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = entityType;
        enemy.add(camCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.isHidden = false;
        posCmp.renderPos = new Vector2(position.x, position.y);
        enemy.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.gravity = true;
        elvCmp.fallTargetY = ClimbFallSystem.TARGET_NOT_CALCULATED;
        elvCmp.elevation = (int) position.z;
        posCmp.elevation = (int) position.z;
        enemy.add(elvCmp);

        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        enemy.add(movCmp);

        HealthComponent hlthComponent = entityEngine.createComponent(HealthComponent.class);
        hlthComponent.init(50);
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

        EntityFactory.initPhysics(enemy);
        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        b2dCmp.body.setUserData(enemy);
        dtctrCmp.detector = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        enemy.add(dtctrCmp);

        b2dCmp.body.setTransform(posCmp.renderPos, 0);

        SteeringComponent steerCmp = entityEngine.createComponent(SteeringComponent.class);
        steerCmp.setOwner(enemy);
        enemy.add(steerCmp);

        BehaviorTreeComponent btCmp = entityEngine.createComponent(BehaviorTreeComponent.class);
        btCmp.tree = BehaviorTreeFactory.createBasicNpcTree(enemy);
        enemy.add(btCmp);

        EntityFactory.attachLight(enemy, new Vector2(2, 2), 5f, Color.RED, 32);

        entityEngine.addEntity(enemy);
        return enemy;
    }

    private void typeStr(Entity enemy, EntityTypeComponent typeCmp) {
        typeCmp.entityType = entityType;
        typeCmp.entityState = EntityState.idle;
        enemy.add(typeCmp);
    }
}
