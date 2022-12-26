package com.straferdeliberator.input.handlers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.straferdeliberator.input.InputHandler;
import com.straferdeliberator.input.PlayerController;
import com.straferdeliberator.input.UIController;

public class DesktopInputHandler extends InputAdapter implements InputHandler {

	public DesktopInputHandler() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void process(PlayerController playerController) {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(UIController uiController) {
		// TODO Auto-generated method stub

	}
}
