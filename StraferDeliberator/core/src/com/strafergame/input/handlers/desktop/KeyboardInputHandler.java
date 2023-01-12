package com.strafergame.input.handlers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.strafergame.input.InputHandler;
import com.strafergame.input.PlayerControl;
import com.strafergame.input.UIControl;
import com.strafergame.settings.KeyboardMapping;
import com.badlogic.gdx.InputAdapter;

public class KeyboardInputHandler extends InputAdapter implements InputHandler {

	public KeyboardInputHandler() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void process(PlayerControl playerControl) {
		PlayerControl.MOVE_UP = Gdx.input.isKeyPressed(Keys.valueOf(KeyboardMapping.MOVE_UP_KEY));
		PlayerControl.MOVE_DOWN = Gdx.input.isKeyPressed(Keys.valueOf(KeyboardMapping.MOVE_DOWN_KEY));
		PlayerControl.MOVE_LEFT = Gdx.input.isKeyPressed(Keys.valueOf(KeyboardMapping.MOVE_LEFT_KEY));
		PlayerControl.MOVE_RIGHT = Gdx.input.isKeyPressed(Keys.valueOf(KeyboardMapping.MOVE_RIGHT_KEY));

	}

	@Override
	public void process(UIControl uiControl) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

}
