package com.strafergame.game.ecs.system.save;

import java.io.IOException;

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
