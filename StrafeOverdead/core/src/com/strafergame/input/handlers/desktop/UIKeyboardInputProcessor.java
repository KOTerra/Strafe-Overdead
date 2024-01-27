package com.strafergame.input.handlers.desktop;

import com.badlogic.gdx.InputProcessor;
import com.strafergame.input.PlayerControl;
import com.strafergame.input.UIControl;
import com.strafergame.settings.KeyboardMapping;

public class UIKeyboardInputProcessor implements InputProcessor {

    private static UIKeyboardInputProcessor instance;
    private boolean dashKeyPressed = false;

    /**
     * return true only if event handled ,if false, event is passed to the next
     * processor in multiplexer
     */
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == KeyboardMapping.PAUSE_TRIGGER_KEY) {
            UIControl.PAUSE_TRIGGER = true;
            return true;
        }
        if (keycode == KeyboardMapping.MAP_TRIGGER_KEY) {
            UIControl.MAP_TRIGGER = true;
            return true;
        }
        //pass la alt processor in multiplexer
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == KeyboardMapping.PAUSE_TRIGGER_KEY) {
            UIControl.PAUSE_TRIGGER = false;
            return true;
        }
        if (keycode == KeyboardMapping.MAP_TRIGGER_KEY) {
            UIControl.MAP_TRIGGER = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {

        return true;
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
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
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

    public static UIKeyboardInputProcessor getInstance() {
        if (instance == null) {
            instance = new UIKeyboardInputProcessor();
        }
        return instance;
    }

}
