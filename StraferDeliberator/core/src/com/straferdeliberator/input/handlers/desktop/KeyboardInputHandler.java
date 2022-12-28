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
