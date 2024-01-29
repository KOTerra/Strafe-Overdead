package com.strafergame.ui.menus;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.screens.TitleScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

public class PauseMenu extends Table {
    private static PauseMenu instance;
    VisTextButton resumeButton;
    VisTextButton titleScreenButton;

    public PauseMenu() {

        resumeButton = new VisTextButton(Strafer.i18n.get("resumeButton"));
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.getInstance().getStateMachine().changeState(GameStateType.PLAY);
                Strafer.uiManager.resumeTrigger();
            }
        });

        titleScreenButton = new VisTextButton(Strafer.i18n.get("titleScreenButton"));
        titleScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.getInstance().getStateMachine().changeState(GameStateType.MENU);
                Strafer.uiManager.emptyTrigger();
                Strafer.getInstance().setScreen(TitleScreen.getInstance());
            }
        });


        setFillParent(true);
        pad(150);
        defaults().space(20);
        align(Align.left);

        Strafer.uiManager.addFocusableActor(resumeButton);
        Strafer.uiManager.setFocusedActor(resumeButton);
        Strafer.uiManager.setEscapeActor(resumeButton);
        add(resumeButton).row();
        Strafer.uiManager.addFocusableActor(titleScreenButton);
        add(titleScreenButton).row();
    }

    @Override
    public void setVisible(boolean a) {
        if (a) {
           // resumeButton.addAction(Actions.sequence(alpha(0), delay(0.f), fadeIn(0.1f, Interpolation.fade)));
           // titleScreenButton.addAction(Actions.sequence(alpha(0), delay(0.f), fadeIn(0.1f, Interpolation.fade)));
        }
        super.setVisible(a);
        Strafer.uiManager.setFocusedActor(resumeButton);
    }

    public static PauseMenu getInstance() {
        if (instance == null) {
            instance = new PauseMenu();
        }
        return instance;
    }
}
