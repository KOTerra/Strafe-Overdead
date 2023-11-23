package com.strafergame.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.screens.SettingsScreen;
import com.strafergame.screens.TitleScreen;

public class GameOverMenu extends Table {

    Strafer game;
    VisTextButton titleScreenButton =new VisTextButton("Title Screen");
    VisTextButton retryButton=new VisTextButton("Retry");
    public GameOverMenu(  ){
        Strafer.uiManager.addActor(this);
        this.game=Strafer.getInstance();
        titleScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTitleMenu();
            }
        });
        add(retryButton);
        row();
        add(titleScreenButton);
        retryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               // Strafer.gameScreen.getGameWorld().reset();
                game.setScreen(Strafer.gameScreen);
            }
        });
    }

    private void showTitleMenu(){
        if (Strafer.titleScreen == null) {
            Strafer.titleScreen = new TitleScreen();
        }
        game.setScreen(Strafer.titleScreen);
    }
}
