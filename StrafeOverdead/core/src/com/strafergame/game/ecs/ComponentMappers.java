package com.strafergame.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;

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

	private ComponentMappers() {
	}

}
