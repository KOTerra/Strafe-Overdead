package com.straferdeliberator.game.entity;

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
	PLAYER;

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
