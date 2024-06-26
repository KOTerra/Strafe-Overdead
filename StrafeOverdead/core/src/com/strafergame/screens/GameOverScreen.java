package com.strafergame.screens;

import com.badlogic.gdx.Screen;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.GameOverMenu;

public class GameOverScreen implements Screen {

    private static GameOverScreen instance;

    GameOverMenu menu;

    public GameOverScreen() {
        menu = new GameOverMenu();
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

    public static GameOverScreen getInstance() {
        if (instance == null) {
            instance = new GameOverScreen();
        }
        return instance;
    }
}
