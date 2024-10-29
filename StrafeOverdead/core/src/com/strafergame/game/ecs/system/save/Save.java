package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strafergame.screens.GameScreen;
import com.strafergame.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

public class Save extends Entity {

    private String fileName;
    private FileHandle fileHandle;
    private File file;

    private int slotIndex;
    private int saveIndex;

    private boolean justCreated = false;
    private Instant created;
    private Instant lastSaved;

    private static final Json json = new Json();

    private HashMap<String, SaveRecord> records = new HashMap<>();

    public Save(int slotIndex, int saveIndex) {
        this.slotIndex = slotIndex;
        this.saveIndex = saveIndex;

        Gdx.files.external(".strafedevs/saves/slot_" + slotIndex).mkdirs();

        fileName = ".strafedevs/saves/slot_" + slotIndex + "/save_" + saveIndex + ".sav";  //+_snapshot_datehourtimesmth
        fileHandle = Gdx.files.external(fileName);
        file = fileHandle.file();
        try {
            justCreated = file.createNewFile();
            if (justCreated) {
                System.out.println("\nJust created save_" + slotIndex + "_" + saveIndex);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(file.getAbsolutePath() + "\n");
        Settings.getPreferences().putInteger("HIGHEST_SAVE_INDEX_ON_SLOT_" + slotIndex, Math.max(Settings.getPreferences().getInteger("HIGHEST_SAVE_INDEX_ON_SLOT_" + slotIndex, 0), saveIndex));
        Settings.getPreferences().putInteger("HIGHEST_SAVE_SLOT", Math.max(Settings.getPreferences().getInteger("HIGHEST_SAVE_SLOT", 0), slotIndex));
        Settings.getPreferences().putInteger("LAST_SAVE_INDEX_ON_SLOT_" + slotIndex, saveIndex);
        Settings.getPreferences().putInteger("LAST_USED_SAVE_SLOT", slotIndex);
        Settings.getPreferences().flush();
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
            registerIfAbsent("FIRST_CREATED_SECONDS", created.getEpochSecond(), Long.class); //register to metasavedataa
        }

        lastSaved = Instant.now();
        register("LAST_SAVED_SECONDS", lastSaved.getEpochSecond(), Long.class);
        System.out.println("\nSaved at: " + Date.from(lastSaved));

        fileHandle.writeString(json.toJson(records), false);

        GameScreen.scheduleScreenshot(".strafedevs/saves/slot_" + slotIndex + "/save_" + saveIndex + ".png");

        lastSaved = Instant.now();
    }


    public void deserialize() {
        records.clear();
        String jsonString = fileHandle.readString();

        if (!jsonString.equals("{}")) {
            records = json.fromJson(HashMap.class, jsonString);
            if (records != null) {
                created = Instant.ofEpochSecond(SaveSystem.retrieveFromRecords("FIRST_CREATED_SECONDS"));

                Instant savedAt = Instant.ofEpochSecond(SaveSystem.retrieveFromRecords("LAST_SAVED_SECONDS"));
                System.err.println("\nLoaded save from: " + Date.from(savedAt) + "\n");
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

    public Instant getLastSavedInstant() {
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
