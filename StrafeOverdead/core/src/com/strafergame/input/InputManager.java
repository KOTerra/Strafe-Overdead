package com.strafergame.input;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;
import com.strafergame.Strafer;
import com.strafergame.input.handlers.controller.ControllerInputHandler;
import com.strafergame.input.handlers.controller.UIControllerInputHandler;
import com.strafergame.input.handlers.desktop.KeyboardInputProcessor;
import com.strafergame.input.handlers.desktop.UIKeyboardInputProcessor;

/**
 * decides and updates which type of input processors to use (mobile, desktop or
 * controller)
 *
 * @author mihai_stoica
 */
public class InputManager {

    InputMultiplexer inputMultiplexer = new InputMultiplexer();


    public InputManager() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        decideOnHandler();
    }

    private void decideOnHandler() {
        ApplicationType appType = Gdx.app.getType();
        if (appType.equals(ApplicationType.Android) || appType.equals(ApplicationType.iOS)) {

        } else {
            inputMultiplexer.clear();
            inputMultiplexer.setProcessors(Strafer.uiManager,UIKeyboardInputProcessor.getInstance(), KeyboardInputProcessor.getInstance());
        }
        if (Controllers.getControllers().notEmpty()) {
            Controllers.addListener(UIControllerInputHandler.getInstance());
            Controllers.addListener(ControllerInputHandler.getInstance());
        }
    }

}
