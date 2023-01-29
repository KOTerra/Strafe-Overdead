package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.component.CameraComponent;

public class CameraSystem extends IteratingSystem {

	public CameraSystem() {
		super(Family.all(CameraComponent.class).get());
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// TODO Auto-generated method stub

	}

}
