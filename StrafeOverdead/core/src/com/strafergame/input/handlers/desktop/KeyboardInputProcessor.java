package com.strafergame.input.handlers.desktop;

import com.badlogic.gdx.InputProcessor;
import com.strafergame.input.PlayerControl;
import com.strafergame.settings.KeyboardMapping;

public class KeyboardInputProcessor implements InputProcessor {

	public KeyboardInputProcessor() {
	}

	/**
	 * return true only if event handled ,if false, event is passed to the next
	 * processor in multiplexer
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == KeyboardMapping.MOVE_UP_KEY) {
			PlayerControl.MOVE_UP = true;
		}
		if (keycode == KeyboardMapping.MOVE_DOWN_KEY) {
			PlayerControl.MOVE_DOWN = true;
		}
		if (keycode == KeyboardMapping.MOVE_LEFT_KEY) {
			PlayerControl.MOVE_LEFT = true;
		}
		if (keycode == KeyboardMapping.MOVE_RIGHT_KEY) {
			PlayerControl.MOVE_RIGHT = true;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == KeyboardMapping.MOVE_UP_KEY) {
			PlayerControl.MOVE_UP = false;
		}
		if (keycode == KeyboardMapping.MOVE_DOWN_KEY) {
			PlayerControl.MOVE_DOWN = false;
		}
		if (keycode == KeyboardMapping.MOVE_LEFT_KEY) {
			PlayerControl.MOVE_LEFT = false;
		}
		if (keycode == KeyboardMapping.MOVE_RIGHT_KEY) {
			PlayerControl.MOVE_RIGHT = false;
		}
		return true;
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