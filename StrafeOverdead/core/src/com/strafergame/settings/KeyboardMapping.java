package com.strafergame.settings;

import com.badlogic.gdx.Input.Keys;

public class KeyboardMapping {
	public static int MOVE_UP_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_UP", "W"));
	public static int MOVE_DOWN_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_DOWN", "S"));
	public static int MOVE_LEFT_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_LEFT", "A"));
	public static int MOVE_RIGHT_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_RIGHT", "D"));
}
