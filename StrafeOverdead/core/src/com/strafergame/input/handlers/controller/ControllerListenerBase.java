package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;

public class ControllerListenerBase implements ControllerListener {

	@Override
	public boolean buttonDown(Controller controller, int buttonIndex) {
		return controller.getButton(buttonIndex);
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonIndex) {
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisIndex, float value) {

		float axis = controller.getAxis(axisIndex);
		//return value > 0 ? axis >= value : axis <= value;
		return true;
	}

	@Override
	public void connected(Controller controller) {
		
	}

	@Override
	public void disconnected(Controller controller) {
	}
}
