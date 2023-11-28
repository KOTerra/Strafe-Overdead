package com.strafergame.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.screens.TitleScreen;

public class PauseMenu extends Table {
    private static PauseMenu instance;

    public PauseMenu() {

        VisTextButton resumeButton = new VisTextButton(Strafer.i18n.get("resumeButton"));
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.getInstance().getStateMachine().changeState(GameStateType.PLAY);
                Strafer.uiManager.resumeTrigger();
            }
        });

        VisTextButton titleScreenButton = new VisTextButton(Strafer.i18n.get("titleScreenButton"));
        titleScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.getInstance().getStateMachine().changeState(GameStateType.MENU);
                Strafer.uiManager.titleTrigger();
                Strafer.getInstance().setScreen(TitleScreen.getInstance());
            }
        });


        setFillParent(true);
        pad(150);
        defaults().space(20);
        align(Align.left);

        add(resumeButton).row();
        add(titleScreenButton).row();
    }


    public static PauseMenu getInstance() {
        if (instance == null) {
            instance = new PauseMenu();
        }
        return instance;
    }
}
