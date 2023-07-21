package com.strafergame.game.ecs.system.camera;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.DetectorComponent;
import com.strafergame.game.ecs.system.combat.ProximityContactPair;
import com.strafergame.game.world.GameWorld;
import com.strafergame.graphics.WorldCamera;

public class CameraSystem extends IteratingSystem {

	private WorldCamera cam = Strafer.worldCamera;

	public CameraSystem() {
		super(Family.all(CameraComponent.class, DetectorComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		DetectorComponent dtctrCmp = ComponentMappers.detector().get(entity);
		CameraComponent camCmp = ComponentMappers.camera().get(entity);
		if (ProximityContactPair.isPlayerInProximity(dtctrCmp)) {
			switch (camCmp.type) {
			case dummy: {
				cam.addToFocus(entity);
				break;
			}
			case checkpoint: {
				cam.setFocusOn(entity);
				break;
			}
			default:
				break;
			}
		} else {
			cam.setFocusOn(GameWorld.player);
		}
	}

}
