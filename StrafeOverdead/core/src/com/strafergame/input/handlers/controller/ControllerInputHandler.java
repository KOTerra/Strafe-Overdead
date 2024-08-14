package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.strafergame.input.PlayerControl;
import com.strafergame.input.UIControl;
import com.strafergame.settings.KeyboardMapping;

public class ControllerInputHandler implements ControllerListener {

    private static ControllerInputHandler instance;

    private final float DEADZONE = .25f;

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        int keycode = -1;
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.JUMP = true;
            keycode = KeyboardMapping.JUMP_KEY;
        }
        if (buttonIndex == controller.getMapping().buttonA) {
            PlayerControl.DASH = true;
            keycode = KeyboardMapping.DASH_KEY;
        }
        if (keycode != -1) {
            PlayerControl.actionSequence.addFirst(new PlayerControl.ActionSequenceElement(keycode, System.currentTimeMillis()));
        }
        return keycode != -1;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.JUMP = false;
        }
        if (buttonIndex == controller.getMapping().buttonA) {
            PlayerControl.DASH = false;
        }

        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        int keycode = -1;
        switch (axisIndex) {
            case 0: {
                if (value > DEADZONE) {
                    PlayerControl.MOVE_RIGHT = true;
                    PlayerControl.MOVE_LEFT = false;
                    keycode = KeyboardMapping.MOVE_RIGHT_KEY;
                }
                if (value < -DEADZONE) {
                    PlayerControl.MOVE_LEFT = true;
                    PlayerControl.MOVE_RIGHT = false;
                    keycode = KeyboardMapping.MOVE_LEFT_KEY;

                }
                if (value >= -DEADZONE && value <= DEADZONE) {
                    PlayerControl.MOVE_RIGHT = false;
                    PlayerControl.MOVE_LEFT = false;
                }
                break;
            }
            case 1: {
                if (value > DEADZONE) {
                    PlayerControl.MOVE_DOWN = true;
                    PlayerControl.MOVE_UP = false;
                    keycode = KeyboardMapping.MOVE_DOWN_KEY;
                }
                if (value < -DEADZONE) {
                    PlayerControl.MOVE_UP = true;
                    PlayerControl.MOVE_DOWN = false;
                    keycode = KeyboardMapping.MOVE_UP_KEY;
                }
                if (value >= -DEADZONE && value <= DEADZONE) {
                    PlayerControl.MOVE_UP = false;
                    PlayerControl.MOVE_DOWN = false;

                }
                break;
            }
        }
        if (keycode != -1) {
            PlayerControl.actionSequence.addFirst(new PlayerControl.ActionSequenceElement(keycode, System.currentTimeMillis()));
        }
        return keycode != -1;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {
    }

    public static ControllerInputHandler getInstance() {
        if (instance == null) {
            instance = new ControllerInputHandler();
        }
        return instance;
    }

}
