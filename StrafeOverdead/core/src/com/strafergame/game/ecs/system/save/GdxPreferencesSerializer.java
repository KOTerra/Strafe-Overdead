package com.strafergame.game.ecs.system.save;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.strafergame.settings.Settings;

public abstract class GdxPreferencesSerializer {

	static Preferences preferences = Settings.getPreferences();
	private static final Json json = new Json();

	private GdxPreferencesSerializer() {
	}

	public static <T> void saveToPreferences(String key, T object, Class<T> objectType) {
		preferences.putString(key, json.toJson(object, objectType));
		preferences.flush();
	}

	public static <T> T loadFromPreferences(Class<T> objectType, String preferencesKey) {
		String jsonString = preferences.getString(preferencesKey, null);
		if (jsonString == null) {
			return null;
		}
		return json.fromJson(objectType, jsonString);
	}
}