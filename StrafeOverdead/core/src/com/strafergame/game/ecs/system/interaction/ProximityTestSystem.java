package com.strafergame.game.ecs.system.interaction;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.PlayerComponent;

public class ProximityTestSystem extends IteratingSystem {

	public ProximityTestSystem() {
		super(Family.all(DetectorComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DetectorComponent dtctrCmp = ComponentMappers.detector().get(entity);
		Entity player = ProximityContact.getPlayerInProximity(dtctrCmp);
		if (player != null) {
			PlayerComponent plyrCmp = ComponentMappers.player().get(player);
			// System.err.println(plyrCmp.baseSpeed);
		}
		if (ProximityContact.isPlayerInProximity(dtctrCmp)) {
			// System.err.println(entity.hashCode());
		}
	}

}
