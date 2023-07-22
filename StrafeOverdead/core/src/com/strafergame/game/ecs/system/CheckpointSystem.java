package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.CheckpointComponent;
import com.strafergame.game.ecs.component.DetectorComponent;
import com.strafergame.game.ecs.system.combat.ProximityContactPair;

public class CheckpointSystem extends IteratingSystem {

	public CheckpointSystem() {
		super(Family.all(CheckpointComponent.class, DetectorComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CheckpointComponent chkCmp = ComponentMappers.checkpoint().get(entity);
		DetectorComponent dtctrCmp = ComponentMappers.detector().get(entity);
		if (ProximityContactPair.isPlayerInProximity(dtctrCmp)) {
			chkCmp.action.execute();
		}
	}

}
