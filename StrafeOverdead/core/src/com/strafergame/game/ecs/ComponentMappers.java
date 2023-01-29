package com.strafergame.game.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.CollisionComponent;
import com.strafergame.game.ecs.component.DirectionComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;

public class ComponentMappers {

	public static final ComponentMapper<AnimationComponent> animation = ComponentMapper
			.getFor(AnimationComponent.class);

	public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);

	public static final ComponentMapper<CollisionComponent> collision = ComponentMapper
			.getFor(CollisionComponent.class);

	public static final ComponentMapper<DirectionComponent> direction = ComponentMapper
			.getFor(DirectionComponent.class);

	public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);

	public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);

	public static final ComponentMapper<SpriteComponent> sprite = ComponentMapper.getFor(SpriteComponent.class);

	private ComponentMappers() {
	}
}
