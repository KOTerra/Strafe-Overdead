package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.world.CheckpointComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.system.interaction.ProximityContact;

public class CheckpointSystem extends IteratingSystem {

	public CheckpointSystem() {
		super(Family.all(CheckpointComponent.class, DetectorComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CheckpointComponent chkCmp = ComponentMappers.checkpoint().get(entity);
		DetectorComponent dtctrCmp = ComponentMappers.detector().get(entity);
		if (ProximityContact.isPlayerInProximity(dtctrCmp)) {
			chkCmp.action.execute();
		}
	}

}
