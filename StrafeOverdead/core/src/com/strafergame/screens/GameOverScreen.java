package com.strafergame.screens;

import com.badlogic.gdx.Screen;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.GameOverMenu;

public class GameOverScreen implements Screen {

    Strafer game;
    GameOverMenu menu;

    public GameOverScreen(Strafer game) {
        this.game = game;
        menu=new GameOverMenu(game);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
