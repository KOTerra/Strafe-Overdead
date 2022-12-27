package com.straferdeliberator.input.handlers.controller;

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
		System.err.println(value);
		return Math.abs(controller.getAxis(axisIndex)) >= value;

	}

	@Override
	public void connected(Controller controller) {
	}

	@Override
	public void disconnected(Controller controller) {
	}
}
