package com.strafergame.game.entities;

/**
 * a state in which an entity can be at a given time
 * 
 * @author mihai_stoica
 *
 */
public enum EntityState {
	/**
	 * the IDLE state
	 */
	IDLE,
	/**
	 * the WALK state
	 */
	WALK,
	/**
	 * the RUN state
	 */
	RUN,
	/**
	 * the DEATH state
	 */
	DEATH;

	public static String asString(EntityState e) {
		String string = "idle";
		switch (e) {
		case IDLE: {
			string = "idle";
			break;
		}
		case WALK: {
			string = "walk";
			break;
		}
		case RUN: {
			string = "run";
			break;
		}
		case DEATH: {
			string = "death";
			break;
		}
		}
		return string;
	}
}
