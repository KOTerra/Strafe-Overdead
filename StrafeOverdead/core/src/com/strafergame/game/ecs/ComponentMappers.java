package com.strafergame.game.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.*;
import com.strafergame.game.ecs.component.world.*;

public abstract class ComponentMappers {


    public static final ComponentMapper<EntityTypeComponent> entityType() {
        return ComponentMapper.getFor(EntityTypeComponent.class);
    }

    public static final ComponentMapper<SpriteComponent> sprite() {
        return ComponentMapper.getFor(SpriteComponent.class);
    }

    public static final ComponentMapper<AnimationComponent> animation() {
        return ComponentMapper.getFor(AnimationComponent.class);
    }

    public static final ComponentMapper<CameraComponent> camera() {
        return ComponentMapper.getFor(CameraComponent.class);
    }

    public static final ComponentMapper<PositionComponent> position() {
        return ComponentMapper.getFor(PositionComponent.class);
    }

    public static final ComponentMapper<MovementComponent> movement() {
        return ComponentMapper.getFor(MovementComponent.class);
    }

    public static final ComponentMapper<Box2dComponent> box2d() {
        return ComponentMapper.getFor(Box2dComponent.class);
    }

    public static final ComponentMapper<PlayerComponent> player() {
        return ComponentMapper.getFor(PlayerComponent.class);
    }

    public static final ComponentMapper<HealthComponent> health() {
        return ComponentMapper.getFor(HealthComponent.class);
    }

    public static final ComponentMapper<AttackComponent> attack() {
        return ComponentMapper.getFor(AttackComponent.class);
    }

    public static final ComponentMapper<DetectorComponent> detector() {
        return ComponentMapper.getFor(DetectorComponent.class);
    }

    public static final ComponentMapper<SteeringComponent> steering() {
        return ComponentMapper.getFor(SteeringComponent.class);
    }

    public static final ComponentMapper<ItemComponent> item() {
        return ComponentMapper.getFor(ItemComponent.class);
    }

    public static final ComponentMapper<AttachmentComponent> attach() {
        return ComponentMapper.getFor(AttachmentComponent.class);
    }

    public static final ComponentMapper<ElevationAgentComponent> elevationAgent() {
        return ComponentMapper.getFor(ElevationAgentComponent.class);
    }

    public static final ComponentMapper<ActivatorComponent> activator() {
        return ComponentMapper.getFor(ActivatorComponent.class);
    }

    public static final ComponentMapper<ElevationComponent> elevation() {
        return ComponentMapper.getFor(ElevationComponent.class);
    }

    public static ComponentMapper<ShadowComponent> shadow() {
        return ComponentMapper.getFor(ShadowComponent.class);
    }

    public static final ComponentMapper<CheckpointComponent> checkpoint() {
        return ComponentMapper.getFor(CheckpointComponent.class);
    }

    public static final ComponentMapper<MapLayerComponent> mapLayer() {
        return ComponentMapper.getFor(MapLayerComponent.class);
    }

    @SuppressWarnings("rawtypes")
    public static final ComponentMapper<AutoSaveComponent> save() {
        return ComponentMapper.getFor(AutoSaveComponent.class);
    }

    public static final ComponentMapper<StatsComponent> stats() {
        return ComponentMapper.getFor(StatsComponent.class);
    }


    private ComponentMappers() {
    }


}
