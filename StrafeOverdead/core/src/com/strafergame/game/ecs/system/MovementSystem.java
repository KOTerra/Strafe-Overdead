package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.PositionComponent;

public class MovementSystem extends IteratingSystem {

	public MovementSystem() {
		super(Family.all(Box2dComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Box2dComponent b2dCmp = ComponentMappers.box2d.get(entity);
		PositionComponent posCmp = ComponentMappers.position.get(entity);

	}
	/**
	 * private void initPhysics() { if (!initiatedPhysics) { this.box2DWorld =
	 * gameWorld.getBox2DWorld(); prevX = -currentFrame.getWidth() *
	 * Strafer.SCALE_FACTOR / 2; prevY = -currentFrame.getHeight() *
	 * Strafer.SCALE_FACTOR / 2;
	 * 
	 * body = Box2DHelper.createBody(box2DWorld.getWorld(), currentFrame.getWidth()
	 * * Strafer.SCALE_FACTOR, currentFrame.getWidth() * Strafer.SCALE_FACTOR, 0, 0,
	 * new Vector3(prevX, prevY, 0), BodyType.DynamicBody);
	 * 
	 * initiatedPhysics = true;
	 * 
	 * } }
	 **/

}
