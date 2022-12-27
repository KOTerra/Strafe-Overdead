package com.straferdeliberator.input.handlers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.straferdeliberator.input.InputHandler;
import com.straferdeliberator.input.PlayerControl;
import com.straferdeliberator.input.UIControl;

public class KeyboardInputHandler extends InputAdapter implements InputHandler {

	public KeyboardInputHandler() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void process(PlayerControl playerControl) {
		PlayerControl.MOVE_UP = Gdx.input.isKeyPressed(Keys.W);
		PlayerControl.MOVE_DOWN = Gdx.input.isKeyPressed(Keys.S);
		PlayerControl.MOVE_LEFT = Gdx.input.isKeyPressed(Keys.A);
		PlayerControl.MOVE_RIGHT = Gdx.input.isKeyPressed(Keys.D);

	}

	@Override
	public void process(UIControl uiControl) {
		// TODO Auto-generated method stub

	}
}
