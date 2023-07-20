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
	idle,
	/**
	 * the WALK state
	 */
	walk,
	/**
	 * the RUN state
	 */
	run,
	/**
	 * the DASH state
	 */
	dash,
	/**
	 * the HIT state
	 */
	hit,
	/**
	 * the RECOVER state after a hit
	 */
	recover,
	/**
	 * the DEATH state
	 */
	death;

}
