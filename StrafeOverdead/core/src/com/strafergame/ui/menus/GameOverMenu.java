package com.strafergame.ui.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;


public class GameOverMenu extends Table {
    private static GameOverMenu instance;
    private VisTextButton retryButton;
    private VisTextButton titleScreenButton;
    private VisLabel titleLabel;

    public GameOverMenu() {
        titleLabel = new VisLabel("OVERDEAD");
        titleLabel.setFontScale(4);
        titleLabel.setColor(Color.RED);
        retryButton = new VisTextButton(Strafer.i18n.get("retryButton"));
        titleScreenButton = new VisTextButton(Strafer.i18n.get("titleScreenButton"));

        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.changeState(GameStateType.PLAY);
            }
        });

        titleScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.changeState(GameStateType.TITLE_MENU);
            }
        });


        setFillParent(true);
        pad(150);
        defaults().space(20);
        align(Align.center);

        add(titleLabel).padBottom(50).row();

        Strafer.uiManager.addFocusableActor(retryButton);
        add(retryButton).width(200).row();

        Strafer.uiManager.addFocusableActor(titleScreenButton);
        add(titleScreenButton).width(200).row();
    }

    @Override
    public void setVisible(boolean a) {
        super.setVisible(a);
        if (a) {
            Strafer.uiManager.setFocusedActor(retryButton);
        }
    }

    public static GameOverMenu getInstance() {
        if (instance == null) {
            instance = new GameOverMenu();
        }
        return instance;
    }
}
