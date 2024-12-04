package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.files.FileHandle;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.system.save.data.MetaSaveData;
import com.strafergame.game.ecs.system.save.data.PlayerSaveData;
import com.strafergame.game.ecs.system.save.data.WorldSaveData;
import com.strafergame.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveSystem {

    public static boolean suppressAutosave = false;

    private static List<FileHandle> savesFiles = new ArrayList<>();

    private static Save currentSave;

    private static MetaSaveData metaSaveData = new MetaSaveData();
    private static PlayerSaveData playerSaveData = new PlayerSaveData();
    private static WorldSaveData worldSaveData = new WorldSaveData();


    public static Save getCurrentSave() {
        if (currentSave == null) {
            System.out.println("Loading the most recent save...");
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

    /**
     * if not found, the entity engine creates a new one
     *
     * @param key
     * @param defaultClass
     * @param <T>          the type of the component MUST implement Component
     * @return
     */
    public static <T extends Component> T retrieveComponentFromRecords(String key, Class<T> defaultClass) {
        T result = retrieveFromRecords(key);
        if (result == null) {
            return EntityEngine.getInstance().createComponent(defaultClass);
        }
        return result;
    }

    public static <T> T retrieveFromRecordsNN(String key, Class<T> clazz) {//not null
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
        return result;
    }

    public static boolean noSaveCreated() {
        return currentSave == null;
    }

    public static void setCurrentSave(Save currentSave) {
        SaveSystem.currentSave = currentSave;
    }


    public static PlayerSaveData getPlayerSaveData() {
        return playerSaveData;
    }

    public static MetaSaveData getMetaSaveData() {
        return metaSaveData;
    }

    public static WorldSaveData getWorldSaveData() {
        return worldSaveData;
    }

    public static boolean anySaveFiles() {
        return getSavesFiles().size() > 0;
    }

    private static void populateSavesList(FileHandle savesDirectory) {
        for (FileHandle file : savesDirectory.list()) {
            if (file.isDirectory()) {
                populateSavesList(file);
            } else {
                if (file.path().matches(Save.fileRegex)) {
                    savesFiles.add(file);
                }
            }
        }
    }

    public static List<FileHandle> getSavesFiles() {
        populateSavesList(Save.savesDirectory);
        return savesFiles;
    }


}
