package com.strafergame.game.ecs.system.save;

import com.strafergame.game.ecs.system.save.data.MetaSaveData;
import com.strafergame.game.ecs.system.save.data.PlayerSaveData;
import com.strafergame.game.ecs.system.save.data.WorldSaveData;
import com.strafergame.settings.Settings;

import java.util.HashMap;

public class SaveSystem {
    public static boolean suppressAutosave = false;
    private static Save currentSave;

    private static MetaSaveData metaSaveData = new MetaSaveData();
    private static PlayerSaveData playerSaveData = new PlayerSaveData();
    private static WorldSaveData worldSaveData = new WorldSaveData();


    public static Save getCurrentSave() {
        if (currentSave == null) {
            int slot = Settings.getPreferences().getInteger("LAST_USED_SAVE_SLOT", 1);
            int index = Settings.getPreferences().getInteger("LAST_SAVE_INDEX_ON_SLOT_" + slot, 0);//index on the given slot
            currentSave = new Save(slot, index);
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

    public static <T> T retrieveFromRecordsNN(String key, Class<T> clazz) {
        T result = retrieveFromRecords(key);
        if (result == null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create a new instance of " + clazz.getName(), e);
            }
        }
        return result;
    }

    public static <T> T retrieveFromRecords(String key, T defaultValue) {
        T result = retrieveFromRecords(key);
        if (result == null) {
            return defaultValue;
        }
        defaultValue = null;
        return result;
    }

    public static PlayerSaveData getPlayerSaveData() {
        return playerSaveData;
    }
}
