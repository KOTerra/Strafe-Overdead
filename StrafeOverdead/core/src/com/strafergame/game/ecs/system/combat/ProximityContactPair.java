package com.strafergame.game.ecs.system.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.component.physics.DetectorComponent;

public class ProximityContactPair {
	public Fixture detector;
	public Fixture sensor;

	public ProximityContactPair(Fixture playerSensor, Fixture detector) {
		this.sensor = playerSensor;
		this.detector = detector;
	}

	/**
	 * @return the entity of the detector if it is in proximity of the player
	 */
	public static Entity getEntityInProximity(DetectorComponent dtctrCmp) {
		ProximityContactPair pair = (ProximityContactPair) dtctrCmp.detector.getUserData();
		if (isPlayerInProximity(pair)) {
			return (Entity) pair.detector.getBody().getUserData();
		}
		return null;
	}

	public static Entity getPlayerInProximity(DetectorComponent dtctrCmp) {
		ProximityContactPair pair = (ProximityContactPair) dtctrCmp.detector.getUserData();
		if (isPlayerInProximity(pair)) {
			return (Entity) pair.sensor.getUserData();
		}
		return null;
	}

	public static boolean isPlayerInProximity(DetectorComponent dtctrCmp) {
		ProximityContactPair pair = (ProximityContactPair) dtctrCmp.detector.getUserData();

		return pair != null;
	}

	public static boolean isPlayerInProximity(ProximityContactPair pair) {
		return pair != null ;
	}

}
