package com.straferdeliberator.settings;

public class KeyboardMapping {
	public static String MOVE_UP_KEY = Settings.getPreferences().getString("KEYBOARD_MOVE_UP", "W");
	public static String MOVE_DOWN_KEY = Settings.getPreferences().getString("KEYBOARD_MOVE_DOWN", "S");
	public static String MOVE_LEFT_KEY = Settings.getPreferences().getString("KEYBOARD_MOVE_LEFT", "A");
	public static String MOVE_RIGHT_KEY = Settings.getPreferences().getString("KEYBOARD_MOVE_RIGHT", "D");
}
