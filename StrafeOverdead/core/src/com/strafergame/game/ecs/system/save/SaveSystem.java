package com.strafergame.game.ecs.system.save;

import java.util.Calendar;

public class SaveSystem {
    public static boolean suppressAutosave = false;
    private static Save currentSave;


    public static Save getCurrentSave() {
        if (currentSave == null) {
            currentSave = new Save();
        }
        return currentSave;
    }
}
