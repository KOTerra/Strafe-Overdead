package com.strafergame.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.time.*;

public abstract class Settings {
    private static final Preferences preferences = Gdx.app.getPreferences("strafer_preferences");

    public static final String LANGUAGE = preferences.getString("LANGUAGE", System.getProperty("user.language"));//in settings menu put "(System language)"

    public static Preferences getPreferences() {
        Instant instant = Instant.now();
        ZoneId z = ZoneId.systemDefault();
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, z);
        //System.out.println(ldt);

//		OffsetDateTime odt = ldt.atOffset( z.getRules().getOffset(ldt)) ;
//		ZonedDateTime zdt = odt.atZoneSameInstant( z ) ;
//        System.out.println(zdt);//with region mentioned

        return preferences;
    }
}
