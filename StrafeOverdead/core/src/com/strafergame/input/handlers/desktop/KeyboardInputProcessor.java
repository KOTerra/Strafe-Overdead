package com.strafergame.input.handlers.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.strafergame.input.PlayerControl;
import com.strafergame.settings.KeyboardMapping;

public class KeyboardInputProcessor implements InputProcessor {

    private static KeyboardInputProcessor instance;

    /**
     * return true only if event handled ,if false, event is passed to the next
     * processor in multiplexer
     */
    @Override
    public boolean keyDown(int keycode) {
        boolean handled = false;
        if (keycode == KeyboardMapping.MOVE_UP_KEY) {
            PlayerControl.MOVE_UP = true;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_DOWN_KEY) {
            PlayerControl.MOVE_DOWN = true;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_LEFT_KEY) {
            PlayerControl.MOVE_LEFT = true;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_RIGHT_KEY) {
            PlayerControl.MOVE_RIGHT = true;
            handled = true;
        }
        if (keycode == KeyboardMapping.JUMP_KEY) {
            PlayerControl.JUMP = true;
            handled = true;
        }
        if (keycode == KeyboardMapping.DASH_KEY) {
            PlayerControl.DASH = true;
            handled = true;
        }


        return handled;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean handled = false;
        if (keycode == KeyboardMapping.MOVE_UP_KEY) {
            PlayerControl.MOVE_UP = false;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_DOWN_KEY) {
            PlayerControl.MOVE_DOWN = false;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_LEFT_KEY) {
            PlayerControl.MOVE_LEFT = false;
            handled = true;
        }
        if (keycode == KeyboardMapping.MOVE_RIGHT_KEY) {
            PlayerControl.MOVE_RIGHT = false;
            handled = true;
        }
        if (keycode == KeyboardMapping.JUMP_KEY) {
            PlayerControl.JUMP = false;
            handled = true;
        }
        if (keycode == KeyboardMapping.DASH_KEY) {
            PlayerControl.DASH = false;
            handled = true;
        }
        if (handled) {
            PlayerControl.actionSequence.addFirst(new PlayerControl.ActionSequenceElement(keycode, System.currentTimeMillis()));
        }

        return handled;
    }

    @Override
    public boolean keyTyped(char character) {

        return true;
    }


    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        boolean handled = false;

        return handled;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        boolean handled = false;

        return handled;
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

    public static KeyboardInputProcessor getInstance() {
        if (instance == null) {
            instance = new KeyboardInputProcessor();
        }
        return instance;
    }

    private class inputSequenceElement {

    }
}
