package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strafergame.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.util.HashMap;

public class Save extends Entity {

    //    private int slotIndex;
//    private int saveIndex;
    private String fileName;
    private FileHandle fileHandle;
    private File file;
    private boolean justCreated = false;
    private Instant created;
    private Instant lastSaved;
    private static final Json json = new Json();

    private HashMap<String, SaveRecord> records = new HashMap<>();

    public <T> Save(int slotIndex, int saveIndex) {

//        String slot = Settings.getPreferences().getString("LAST_USED_SAVE_SLOT", "1");
//        slotIndex = Integer.parseInt(slot);

        Gdx.files.external(".strafedevs/saves/slot_" + slotIndex).mkdirs();

//        String index = Settings.getPreferences().getString("LAST_SAVE_INDEX_ON_SLOT_" + slot, "0");//index on the given slot
//        saveIndex = Integer.parseInt(index);// + 1;
        fileName = ".strafedevs/saves/slot_" + slotIndex + "/save_" + saveIndex + ".sav";  //+_snapshot_datehourtimesmth
        fileHandle = Gdx.files.external(fileName);
        file = fileHandle.file();
        try {
            justCreated = file.createNewFile();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(file.getAbsolutePath());
        Settings.getPreferences().putString("LAST_SAVE_INDEX_ON_SLOT_" + slotIndex, Integer.toString(saveIndex));
        Settings.getPreferences().putString("LAST_USED_SAVE_SLOT", Integer.toString(slotIndex)).flush();
    }

    public <T> void register(String key, T object, Class<T> objectType) {
        if (records == null) {
            records = new HashMap<>();
        }
        records.put(key, new SaveRecord<>(key, object, objectType));
    }

    private <T> void registerIfAbsent(String key, T object, Class<T> objectType) {
        if (records == null) {
            records = new HashMap<>();
        }
        records.putIfAbsent(key, new SaveRecord<>(key, object, objectType));

    }

    public void serialize() {
        fileHandle.writeString("", false);

        if (justCreated) {
            created = Instant.now();
        }
        if (created != null) {
            registerIfAbsent("FIRST_CREATED_SECONDS", created.getEpochSecond(), Long.class);
        }

        lastSaved = Instant.now();
        register("LAST_SAVED_SECONDS", lastSaved.getEpochSecond(), Long.class);
        System.out.println("\nSaved at: " + Time.from(lastSaved));
        
        fileHandle.writeString(json.toJson(records), false);
    }


    public <T> void deserialize() {
        records.clear();
        String jsonString = fileHandle.readString();

        if (!jsonString.equals("{}")) {
            records = json.fromJson(HashMap.class, jsonString);
            if (records != null) {
                created = Instant.ofEpochSecond(SaveSystem.retrieveFromRecords("FIRST_CREATED_SECONDS"));

                Instant savedAt = Instant.ofEpochSecond(SaveSystem.retrieveFromRecords("LAST_SAVED_SECONDS"));
                System.err.println("\nLoaded save from: " + Time.from(savedAt) + "\n");
                records.forEach((key, value) -> {
                    SaveRecord record = records.get(key);
                    // System.out.println("deser "+record.object);
                });
            }
        }

    }

    public HashMap<String, SaveRecord> getRecords() {
        if (records == null) {
            records = new HashMap<>();
        }
        return records;
    }

    public Instant getLastSaved() {
        return lastSaved;
    }

    public static class SaveRecord<T> implements Json.Serializable {
        public String key;
        public T object;
        public Class<T> objectType;

        public SaveRecord() {

        }

        public SaveRecord(String key, T object, Class<T> objectType) {
            this.key = key;
            this.object = object;
            this.objectType = objectType;
        }

        public T getObject() {
            return object;
        }


        @Override
        public void write(Json json) {
            json.writeValue(key, object, objectType.getClass());
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            key = jsonData.name();
            System.err.println("\naaaa  " + key);
            try {
                objectType = (Class<T>) Class.forName(jsonData.getString("class"));
                System.err.println("bbbb " + objectType.getName());
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
            object = json.readValue(key, objectType, jsonData);
            System.err.println("cccc " + object);

        }
    }
}
