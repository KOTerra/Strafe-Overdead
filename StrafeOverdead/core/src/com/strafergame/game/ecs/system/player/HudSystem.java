package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.PlayerComponent;

public class HudSystem extends IteratingSystem {

	public HudSystem() {
		super(Family.all(PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		HealthComponent hlthCmp = ComponentMappers.health().get(entity);
		Strafer.uiManager.getHud().getHealthBar().setValue(hlthCmp.hitPoints);
	}

}
