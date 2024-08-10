package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.strafergame.settings.Settings;

import java.time.Instant;
import java.util.HashMap;

public class Save extends Entity {

    private int saveIndex;
    private String fileName;
    private FileHandle file;
    private final Instant created = Instant.now();
    private Instant lastSaved = created;

    private HashMap<String, SaveRecord> records = new HashMap<>();

    public Save() {
        String index=Settings.getPreferences().getString("LAST_SAVE_INDEX","0");
        saveIndex=Integer.parseInt(index)+1;
        fileName="save_"+saveIndex+".sav";  //+_snapshot_datehourtimesmth
        file=new FileHandle(fileName);
    }

    public <T> void register(String key, T object, Class<T> objectType) {
        records.put(key, new SaveRecord<>(key, object, objectType));
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
