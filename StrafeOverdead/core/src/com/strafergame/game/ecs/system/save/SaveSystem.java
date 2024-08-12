package com.strafergame.game.ecs.system.save;

import com.strafergame.settings.Settings;

import java.util.HashMap;

public class SaveSystem {
    public static boolean suppressAutosave = false;
    private static Save currentSave;


    public static Save getCurrentSave() {
        if (currentSave == null) {
            String slot = Settings.getPreferences().getString("LAST_USED_SAVE_SLOT", "1");
            String index = Settings.getPreferences().getString("LAST_SAVE_INDEX_ON_SLOT_" + slot, "0");//index on the given slot
            currentSave = new Save(Integer.parseInt(slot), Integer.parseInt(index));
        }
        return currentSave;
    }

    public static <T> T retrieveFromRecords(String key) {
        HashMap<String, Save.SaveRecord> records = SaveSystem.getCurrentSave().getRecords();
        if (records != null && records.containsKey(key)) {
            Save.SaveRecord<T> record = (Save.SaveRecord<T>) records.get(key);
            if (record != null) {
                return record.getObject();
            }
        }
        return null;
    }
}
