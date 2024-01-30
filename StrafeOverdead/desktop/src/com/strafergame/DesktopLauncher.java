package com.strafergame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {

		GameStateManager.getInstance().getStateMachine().setInitialState(GameStateType.PRE_LOADING);

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(144);
		config.useVsync(true);
		config.setWindowedMode(1280, 720);
		config.setTitle("Strafe Overdead");
		// config.setDecorated(false);
		config.setResizable(true);

		 //config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		new Lwjgl3Application(Strafer.getInstance(), config);

	}
}
