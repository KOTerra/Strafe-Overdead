package com.straferdeliberator.input;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.straferdeliberator.input.handlers.controller.ControllerInputHandler;
import com.straferdeliberator.input.handlers.desktop.DesktopInputHandler;
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
	DesktopInputHandler desktopHandler;
	ControllerInputHandler controllerHandler;

	private PlayerController playerController = new PlayerController();

	private UIController uiController = new UIController();

	public InputManager() {
		decideOnHandler();
	}

	public void processInput() {
		inputHandler.process(playerController);
		inputHandler.process(uiController);

	}

	private void decideOnHandler() {
		ApplicationType appType = Gdx.app.getType();
		if (appType.equals(ApplicationType.Android) || appType.equals(ApplicationType.iOS)) {
			if (mobileHandler == null) {
				mobileHandler = new MobileInputHandler();
			}
			setInputHandler(mobileHandler);
		} else {
			if (desktopHandler == null) {
				desktopHandler = new DesktopInputHandler();
			}
			setInputHandler(desktopHandler);
		}
	}

	public void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
}
