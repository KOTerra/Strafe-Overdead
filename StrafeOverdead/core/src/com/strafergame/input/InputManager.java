package com.strafergame.input;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.strafergame.input.handlers.desktop.KeyboardInputProcessor;

/**
 * decides and updates which type of input processors to use (mobile, desktop or
 * controller)
 * 
 * @author mihai_stoica
 *
 */
public class InputManager {

	InputMultiplexer inputMultiplexer = new InputMultiplexer();

	KeyboardInputProcessor keyboardHandler;

	private final PlayerControl playerControl = new PlayerControl();

	private final UIControl uiController = new UIControl();

	public InputManager() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		decideOnHandler();
	}

	private void decideOnHandler() {
		ApplicationType appType = Gdx.app.getType();
		if (appType.equals(ApplicationType.Android) || appType.equals(ApplicationType.iOS)) {

		} else {
			if (keyboardHandler == null) {
				keyboardHandler = new KeyboardInputProcessor();
			}
			inputMultiplexer.clear();
			inputMultiplexer.addProcessor(keyboardHandler);
		}
		if (Controllers.getControllers().notEmpty()) {
			System.out.println("controller");
		}
	}

}
