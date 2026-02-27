package com.strafergame.input.handlers.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.strafergame.input.PlayerControl;
import com.strafergame.settings.KeyboardMapping;

public class ControllerInputHandler implements ControllerListener {

    private static ControllerInputHandler instance;

    private final float DEADZONE = .4f;

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        PlayerControl.USING_CONTROLLER = true;
        int keycode = -1;

        ///ABXY
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.DASH = true;
            keycode = KeyboardMapping.DASH_KEY;
        }
        if (buttonIndex == controller.getMapping().buttonA) {
            PlayerControl.JUMP = true;
            keycode = KeyboardMapping.JUMP_KEY;
        }
        if (buttonIndex == controller.getMapping().buttonX) {
            PlayerControl.ATTACK = true;
            keycode = KeyboardMapping.ATTACK_KEY;
        }
        if (buttonIndex == controller.getMapping().buttonR1) {
            PlayerControl.SHOOT = true;
            keycode = KeyboardMapping.SHOOT_KEY;
        }

        //Dpad
        if (buttonIndex == controller.getMapping().buttonDpadUp) {
            keycode = Input.Keys.UP;
        }
        if (buttonIndex == controller.getMapping().buttonDpadDown) {
            keycode = Input.Keys.DOWN;
        }
        if (buttonIndex == controller.getMapping().buttonDpadLeft) {
            keycode = Input.Keys.LEFT;
        }
        if (buttonIndex == controller.getMapping().buttonDpadRight) {
            keycode = Input.Keys.RIGHT;
        }

        if (keycode != -1) {
            PlayerControl.actionSequence.addFirst(new PlayerControl.ActionSequenceElement(keycode, System.currentTimeMillis()));
        }
        return keycode != -1;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        ///ABXY
        if (buttonIndex == controller.getMapping().buttonB) {
            PlayerControl.DASH = false;
        }
        if (buttonIndex == controller.getMapping().buttonA) {
            PlayerControl.JUMP = false;
        }
        if (buttonIndex == controller.getMapping().buttonX) {
            PlayerControl.ATTACK = false;
        }
        if (buttonIndex == controller.getMapping().buttonR1) {
            PlayerControl.SHOOT = false;
        }

        //Dpad
        if (buttonIndex == controller.getMapping().buttonDpadUp) {
        }
        if (buttonIndex == controller.getMapping().buttonDpadDown) {
        }
        if (buttonIndex == controller.getMapping().buttonDpadLeft) {
        }
        if (buttonIndex == controller.getMapping().buttonDpadRight) {
        }

        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        if (Math.abs(value) > DEADZONE) {
            PlayerControl.USING_CONTROLLER = true;
        }

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
            // RIGHT STICK AIMING
            case 2:
            case 3: {
                float rx = controller.getAxis(2);
                float ry = -controller.getAxis(3); // inverted Y

                float magnitude = (float) Math.sqrt(rx * rx + ry * ry);

                if (magnitude > DEADZONE) {
                    PlayerControl.CONTROLLER_AIM_DIRECTION.set(rx, ry);
                } else {
                    PlayerControl.CONTROLLER_AIM_DIRECTION.set(0, 0);
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
        PlayerControl.USING_CONTROLLER = true;
    }

    @Override
    public void disconnected(Controller controller) {
        PlayerControl.USING_CONTROLLER = false;
    }

    public static ControllerInputHandler getInstance() {
        if (instance == null) {
            instance = new ControllerInputHandler();
        }
        return instance;
    }

}