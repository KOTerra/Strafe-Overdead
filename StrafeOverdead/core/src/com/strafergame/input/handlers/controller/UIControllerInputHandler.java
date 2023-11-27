package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.strafergame.input.PlayerControl;
import com.strafergame.input.UIControl;

public class UIControllerInputHandler implements ControllerListener {

    private static UIControllerInputHandler instance;

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if (buttonIndex == controller.getMapping().buttonStart) {
            UIControl.PAUSE_TRIGGER = true;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            UIControl.MAP_TRIGGER = true;
        }
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        if (buttonIndex == controller.getMapping().buttonStart) {
            UIControl.PAUSE_TRIGGER = false;
        }
        if (buttonIndex == controller.getMapping().buttonBack) {
            UIControl.MAP_TRIGGER = false;
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    public static UIControllerInputHandler getInstance() {
        if (getInstance() == null) {
            instance = new UIControllerInputHandler();
        }
        return instance;
    }
}
