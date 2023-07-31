package com.strafergame.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public abstract class Settings {
	private static final Preferences preferences = Gdx.app.getPreferences("strafer_preferences");

	public final String LANGUAGE = preferences.getString("LANGUAGE", "ro");

	public static Preferences getPreferences() {
		return preferences;
	}
}
