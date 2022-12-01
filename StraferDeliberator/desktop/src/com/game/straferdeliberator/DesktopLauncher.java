package com.game.straferdeliberator;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.straferdeliberator.Strafer;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(144);
		
		config.setWindowedMode(1280,720);
		config.setTitle("Strafer Deliberator");
		config.setDecorated(true);
		config.setResizable(true);
		
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode() );
		new Lwjgl3Application(new Strafer(), config);
		

	}
}
