package com.strafergame.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public abstract class Settings {
	private static final Preferences preferences = Gdx.app.getPreferences("strafer_preferences");

	private final String LANGUAGE = preferences.getString("LANGUAGE", "en");

	public static Preferences getPreferences() {
		return preferences;
	}
}
