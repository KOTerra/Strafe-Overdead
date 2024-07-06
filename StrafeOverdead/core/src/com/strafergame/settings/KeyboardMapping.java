package com.strafergame.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class KeyboardMapping {
    public static int MOVE_UP_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_UP", "W"));
    public static int MOVE_DOWN_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_DOWN", "S"));
    public static int MOVE_LEFT_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_LEFT", "A"));
    public static int MOVE_RIGHT_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MOVE_RIGHT", "D"));
    public static int JUMP_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_JUMP", "Space"));
    public static int DASH_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_DASH", "L-Shift"));

    public static int PAUSE_TRIGGER_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_PAUSE_TRIGGER", "Escape"));
    public static int MAP_TRIGGER_KEY = Keys.valueOf(Settings.getPreferences().getString("KEYBOARD_MAP_TRIGGER", "Tab"));

    public static int[] KONAMI_CODE_SEQUENCE = makeSequenceMapping("KONAMI_CODE",
            "Up", "Up", "Down", "Down", "Left", "Right", "Left", "Right", "B", "A");


    private static int[] makeSequenceMapping(String settingsKey, String... strings) {
        int[] result = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String key = Settings.getPreferences().getString(settingsKey + '_' + i, strings[i]);
            switch (key) {
                case "MOUSE_LEFT": {
                    result[i] = Input.Buttons.LEFT;
                    break;
                }
                case "MOUSE_RIGHT": {
                    result[i] = Input.Buttons.RIGHT;
                    break;
                }
                case "MOUSE_MIDDLE": {
                    result[i] = Input.Buttons.MIDDLE;
                    break;
                }
                case "MOUSE_FORWARD": {
                    result[i] = Input.Buttons.FORWARD;
                    break;
                }
                case "MOUSE_BACK": {
                    result[i] = Input.Buttons.BACK;
                    break;
                }
                default: {
                    result[i] = Keys.valueOf(key);
                }
            }
        }
        return result;
    }
}
