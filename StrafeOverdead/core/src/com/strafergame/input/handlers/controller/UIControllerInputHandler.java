package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.strafergame.input.UIControl;
import com.strafergame.ui.UiManager;

public class UIControllerInputHandler implements ControllerListener {

    private static UIControllerInputHandler instance;

    private final float DEADZONE = .25f;


    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {

        boolean handled = false;
        if (buttonIndex == controller.getMapping().buttonStart) {   ///pt meniuri ar trb ca B sa fie back, B si Start sa fie separate
            UIControl.PAUSE_TRIGGER = true;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            UIControl.MAP_TRIGGER = true;
            handled = true;

        }
        if (!UiManager.canControlUI()) {    //HudStates
            return false;
        }
        if (buttonIndex == controller.getMapping().buttonDpadDown) {
            UIControl.DOWN_SELECT = true;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonDpadUp) {
            UIControl.UP_SELECT = true;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonDpadRight) {
            UIControl.RIGHT_SELECT = true;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonDpadLeft) {
            UIControl.LEFT_SELECT = true;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonA) {
            UIControl.SELECT = true;
            handled = true;
        }

        if (buttonIndex == controller.getMapping().buttonB) {
            UIControl.BACK = true;
            handled = true;
        }

        return handled;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        boolean handled = false;
        if (buttonIndex == controller.getMapping().buttonStart) {
            UIControl.PAUSE_TRIGGER = false;
            handled = true;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            UIControl.MAP_TRIGGER = false;
            handled = true;
        }
        return handled;
    }


    private boolean yAxisAlreadyMoved = false;
    private boolean xAxisAlreadyMoved = false;

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if (!UiManager.canControlUI()) {
            return false;
        }
        boolean handled = false;

        switch (axisCode) {
            case 0: {
                if (value > DEADZONE) {
                    if (!xAxisAlreadyMoved) {
                        UIControl.RIGHT_SELECT = true;
                        UIControl.LEFT_SELECT = false;

                        xAxisAlreadyMoved = true;
                    }
                } else if (value < -DEADZONE) {
                    if (!xAxisAlreadyMoved) {
                        UIControl.LEFT_SELECT = true;
                        UIControl.RIGHT_SELECT = false;

                        xAxisAlreadyMoved = true;
                    }
                } else {
                    if (xAxisAlreadyMoved) {
                        xAxisAlreadyMoved = false;
                    }
                }
                if (value >= -DEADZONE && value <= DEADZONE) {
                    UIControl.LEFT_SELECT = false;
                    UIControl.RIGHT_SELECT = false;
                }

                handled = true;
                break;
            }
            case 1: {
                if (value > DEADZONE) {
                    if (!yAxisAlreadyMoved) { //////////////////////////////////////////////////////////sau a trecut 1sec sau .5sec timer
                        UIControl.DOWN_SELECT = true;
                        UIControl.UP_SELECT = false;

                        yAxisAlreadyMoved = true;  // Set the flag to ignore subsequent movements
                    }
                } else if (value < -DEADZONE) {
                    if (!yAxisAlreadyMoved) {
                        UIControl.UP_SELECT = true;
                        UIControl.DOWN_SELECT = false;

                        yAxisAlreadyMoved = true;  // Set the flag to ignore subsequent movements
                    }
                } else {
                    if (yAxisAlreadyMoved) {
                        yAxisAlreadyMoved = false;  // Reset the flag
                    }
                }
                if (value >= -DEADZONE && value <= DEADZONE) {
                    UIControl.UP_SELECT = false;
                    UIControl.DOWN_SELECT = false;
                }
                handled = true;
                break;
            }
        }

        return handled;
    }

    public static UIControllerInputHandler getInstance() {
        if (instance == null) {
            instance = new UIControllerInputHandler();
        }
        return instance;
    }
}
