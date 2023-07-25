package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.strafergame.input.PlayerControl;

public class ControllerInputHandler implements ControllerListener {

    private final float DEADZONE=.25f;
    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.DASH = true;
        }
        if (buttonIndex == controller.getMapping().buttonStart) {
            PlayerControl.PAUSE_TRIGGER = true;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            PlayerControl.MAP_TRIGGER = true;
        }
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.DASH = false;
        }
        if (buttonIndex == controller.getMapping().buttonStart) {
            PlayerControl.PAUSE_TRIGGER = false;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            PlayerControl.MAP_TRIGGER = false;
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {

        switch (axisIndex) {
            case 0: {
                if (value > DEADZONE) {
                    PlayerControl.MOVE_RIGHT = true;
                    PlayerControl.MOVE_LEFT = false;

                }
                if (value < -DEADZONE) {
                    PlayerControl.MOVE_LEFT = true;
                    PlayerControl.MOVE_RIGHT = false;
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

                }
                if (value < -DEADZONE) {
                    PlayerControl.MOVE_UP = true;
                    PlayerControl.MOVE_DOWN = false;
                }
                if (value >= -DEADZONE && value <= DEADZONE) {
                    PlayerControl.MOVE_UP = false;
                    PlayerControl.MOVE_DOWN = false;
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {
    }
}
