package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.PlayerComponent;

public class PlayerMovementSystem extends IteratingSystem {

	public PlayerMovementSystem() {
		super(Family.all(PlayerComponent.class, AnimationComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		
	}

}
