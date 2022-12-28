package com.straferdeliberator.input.handlers.mobile;

import com.badlogic.gdx.InputAdapter;
import com.straferdeliberator.input.InputHandler;
import com.straferdeliberator.input.PlayerControl;
import com.straferdeliberator.input.UIControl;

public class MobileInputHandler extends InputAdapter implements InputHandler {

	@Override
	public void process(PlayerControl playerControl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void process(UIControl uiControl) {
		// TODO Auto-generated method stub

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
