package com.straferdeliberator.input;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.Gdx;
import com.straferdeliberator.input.handlers.controller.ControllerInputHandler;
import com.straferdeliberator.input.handlers.desktop.KeyboardInputHandler;
import com.straferdeliberator.input.handlers.mobile.MobileInputHandler;

/**
 * decides and updates which type of input handlers to use (mobile, desktop or
 * controller) and processes it
 * 
 * @author mihai_stoica
 *
 */
public class InputManager {

	InputHandler inputHandler;

	MobileInputHandler mobileHandler;
	KeyboardInputHandler keyboardHandler;
	ControllerInputHandler controllerHandler;

	private PlayerControl playerControl = new PlayerControl();

	private UIControl uiController = new UIControl();

	public InputManager() {
		decideOnHandler();
	}

	public void processInput() {
		inputHandler.process(uiController);
		inputHandler.process(playerControl);
	}

	private void decideOnHandler() {
		ApplicationType appType = Gdx.app.getType();
		if (appType.equals(ApplicationType.Android) || appType.equals(ApplicationType.iOS)) {
			if (mobileHandler == null) {
				mobileHandler = new MobileInputHandler();
			}
			setInputHandler(mobileHandler);
		} else {
			if (keyboardHandler == null) {
				keyboardHandler = new KeyboardInputHandler();
			}
			setInputHandler(keyboardHandler);
		}
		if (Controllers.getControllers().notEmpty()) {
			if (controllerHandler == null) {
				controllerHandler = new ControllerInputHandler();
			}
			setInputHandler(controllerHandler);
		}
	}

	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
}
