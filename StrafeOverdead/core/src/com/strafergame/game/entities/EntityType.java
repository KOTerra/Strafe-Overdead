package com.strafergame.game.entities;

/**
 * the type of an entity
 * 
 * @author mihai_stoica
 *
 */
public enum EntityType {
	/**
	 * the PLAYER type
	 */
	player(10f);

	float speed;

	EntityType(float speed) {
		this.speed = speed;
	}

}
