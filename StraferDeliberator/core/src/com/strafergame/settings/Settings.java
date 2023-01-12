package com.strafergame.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
	private final Preferences preferences = Gdx.app.getPreferences("strafer_preferences");

	private String LANGUAGE = preferences.getString("LANGUAGE", "en");

	public static Preferences getPreferences() {
		return Gdx.app.getPreferences("strafer_preferences");
	}
}
