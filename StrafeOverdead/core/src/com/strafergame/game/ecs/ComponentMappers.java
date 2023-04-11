package com.strafergame.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.DirectionComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;

public abstract class ComponentMappers {

	public static final ComponentMapper<EntityTypeComponent> entityType = ComponentMapper
			.getFor(EntityTypeComponent.class);

	public static final ComponentMapper<SpriteComponent> sprite = ComponentMapper.getFor(SpriteComponent.class);

	public static final ComponentMapper<AnimationComponent> animation = ComponentMapper
			.getFor(AnimationComponent.class);

	public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);

	public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);

	public static final ComponentMapper<DirectionComponent> direction = ComponentMapper
			.getFor(DirectionComponent.class);

	public static final ComponentMapper<Box2dComponent> box2d = ComponentMapper
			.getFor(Box2dComponent.class);

	public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);

	private ComponentMappers() {
	}
}
