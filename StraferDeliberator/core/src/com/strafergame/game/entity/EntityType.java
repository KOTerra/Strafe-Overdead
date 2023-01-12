package com.strafergame.game.entity;

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
	PLAYER(6f);

	float speed;

	private EntityType(float speed) {
		this.speed = speed;
	}

	public static String asString(EntityType e) {
		String string = "";
		switch (e) {
		case PLAYER: {
			string = "player";
			break;
		}
		}
		return string;
	}
}
