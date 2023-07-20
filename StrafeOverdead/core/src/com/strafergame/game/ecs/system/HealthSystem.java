package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.HealthComponent;

public class HealthSystem extends IntervalIteratingSystem {

	public HealthSystem(float interval) {
		super(Family.all(HealthComponent.class, Box2dComponent.class).get(), interval);
	}

	@Override
	protected void processEntity(Entity entity) {
		// TODO Auto-generated method stub

	}

}
