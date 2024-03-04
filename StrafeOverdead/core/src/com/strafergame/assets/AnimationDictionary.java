package com.strafergame.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.strafergame.game.ecs.states.EntityType;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * a class that specifies what kinds of animations are attributed to an entity how the can be loaded and provided
 */
public abstract class AnimationDictionary {

    static HashMap<String, AnimationDictionaryEntry> all = new HashMap<>();//.gettype.getanimation => duration looping etc

    public static void loadEntries() {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(Gdx.files.internal("spritesheets/animation_dictionary.json"));
        for (JsonValue entry : root.child) {
            String key = entry.getString("name");
            float duration = entry.getFloat("duration");
            boolean loop = entry.getBoolean("loop");
            all.put(key, new AnimationDictionaryEntry(duration, loop));
        }
    }

    public static float getDuration(String key) {
        return all.get(key).duration;
    }

    public static boolean isLooping(String key) {
        return all.get(key).loop;
    }

    private static class AnimationDictionaryEntry {
        float duration;
        boolean loop;

        public AnimationDictionaryEntry(float duration, boolean loop) {
            this.duration = duration;
            this.loop = loop;
        }
    }
}
