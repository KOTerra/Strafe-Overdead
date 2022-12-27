package com.straferdeliberator.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.straferdeliberator.input.InputHandler;
import com.straferdeliberator.input.PlayerControl;
import com.straferdeliberator.input.UIControl;

public class ControllerInputHandler implements InputHandler {

	@Override
	public void process(PlayerControl playerControl) {
		Controller c = Controllers.getCurrent();
		ControllerListenerBase cm = new ControllerListenerBase();

		PlayerControl.MOVE_UP = cm.axisMoved(c, c.getMapping().axisLeftY, -1f);
		PlayerControl.MOVE_DOWN = cm.axisMoved(c, c.getMapping().axisLeftY, 1f);
		PlayerControl.MOVE_LEFT = cm.axisMoved(c, c.getMapping().axisLeftX, -1f);
		PlayerControl.MOVE_RIGHT = cm.axisMoved(c, c.getMapping().axisLeftX, 1f);
	}

	@Override
	public void process(UIControl uiControl) {
		// TODO Auto-generated method stub

	}

}
