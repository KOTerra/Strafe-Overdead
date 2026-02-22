package com.strafergame.settings;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

public class KeyboardMapping {

    public static int MOVE_UP_KEY = makeKeyMapping("KEYBOARD_MOVE_UP", "W");
    public static int MOVE_DOWN_KEY = makeKeyMapping("KEYBOARD_MOVE_DOWN", "S");
    public static int MOVE_LEFT_KEY = makeKeyMapping("KEYBOARD_MOVE_LEFT", "A");
    public static int MOVE_RIGHT_KEY = makeKeyMapping("KEYBOARD_MOVE_RIGHT", "D");
    public static int JUMP_KEY = makeKeyMapping("KEYBOARD_JUMP", "Space");
    public static int DASH_KEY = makeKeyMapping("KEYBOARD_DASH", "L-Shift");

    public static int ATTACK_KEY = makeKeyMapping("KEYBOARD_ATTACK", "MOUSE_LEFT");
    public static int SHOOT_KEY = makeKeyMapping("KEYBOARD_SHOOT", "MOUSE_RIGHT");

    public static int PAUSE_TRIGGER_KEY = makeKeyMapping("KEYBOARD_PAUSE_TRIGGER", "Escape");
    public static int MAP_TRIGGER_KEY = makeKeyMapping("KEYBOARD_MAP_TRIGGER", "Tab");

    public static int[] KONAMI_CODE_SEQUENCE = makeSequenceMapping("KONAMI_CODE",
            "Up", "Up", "Down", "Down", "Left", "Right", "Left", "Right", "Space", "L-Shift");

    public static int[] TRIPLE_CLICK_SEQUENCE = makeSequenceMapping("KONAMI_CODE",
            "MOUSE_LEFT", "MOUSE_LEFT", "MOUSE_LEFT");


    private static int makeKeyMapping(String settingsKey, String defaultValue) {
        return getFromName(Settings.getPreferences().getString(settingsKey, defaultValue));
    }

    private static int[] makeSequenceMapping(String settingsKey, String... strings) {
        int[] result = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            String key = Settings.getPreferences().getString(settingsKey + '_' + i, strings[i]);
            result[i] = getFromName(key);
        }
        return result;
    }

    private static int getFromName(String name) {
        return switch (name) {
            case "MOUSE_LEFT" -> Input.Buttons.LEFT;
            case "MOUSE_RIGHT" -> Input.Buttons.RIGHT;
            case "MOUSE_MIDDLE" -> Input.Buttons.MIDDLE;
            case "MOUSE_FORWARD" -> Input.Buttons.FORWARD;
            case "MOUSE_BACK" -> Input.Buttons.BACK;
            default -> Keys.valueOf(name);
        };
    }


}
