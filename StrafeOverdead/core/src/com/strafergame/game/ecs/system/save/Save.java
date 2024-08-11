package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.strafergame.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

public class Save extends Entity {

    private int slotIndex;
    private int saveIndex;
    private String fileName;
    private FileHandle fileHandle;
    private File file;
    private final Instant created = Instant.now();
    private Instant lastSaved = created;
    private static final Json json = new Json();

    private HashMap<String, SaveRecord> records;

    public Save() {
        records = new HashMap<>();

        String slot = Settings.getPreferences().getString("LAST_USED_SAVE_SLOT", "1");
        slotIndex = Integer.parseInt(slot);

        Gdx.files.external(".strafedevs/saves/slot_" + slot).mkdirs();

        String index = Settings.getPreferences().getString("LAST_SAVE_INDEX_ON_SLOT_" + slot, "0");//index on the given slot
        saveIndex = Integer.parseInt(index) + 1;
        fileName = ".strafedevs/saves/slot_" + slot + "/save_" + saveIndex + ".sav";  //+_snapshot_datehourtimesmth
        fileHandle = Gdx.files.external(fileName);
        file = fileHandle.file();
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(file.getAbsolutePath());
        Settings.getPreferences().putString("LAST_SAVE_INDEX_ON_SLOT_" + slot, Integer.toString(saveIndex));
        Settings.getPreferences().putString("LAST_USED_SAVE_SLOT", Integer.toString(slotIndex)).flush();

        deserialize();

    }

    public <T> void register(String key, T object, Class<T> objectType) {
        if (records == null) {
            records = new HashMap<>();
        }
        records.put(key, new SaveRecord<>(key, object, objectType));
    }

    public void serialize() {
        //records.forEach((key, record) -> {
        // fileHandle.writeString(json.toJson(record.object, record.objectType),true);
        // });
        fileHandle.writeString(json.toJson(records, HashMap.class), false);
    }

    public void deserialize() {
        String jsonString = fileHandle.readString();
        if (!jsonString.equals("{}")) {
            records = json.fromJson(HashMap.class, jsonString);
        }
    }

    private static class SaveRecord<T> {
        public String key;
        public T object;
        public Class<T> objectType;

        public SaveRecord(String key, T object, Class<T> objectType) {
            this.key = key;
            this.object = object;
            this.objectType = objectType;
        }
    }
}
