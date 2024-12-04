package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.LoadSaveMenu;

public class LoadSaveScreen implements Screen {

    private static LoadSaveScreen instance;

    private LoadSaveMenu loadSaveMenu;

    private LoadSaveScreen() {
        loadSaveMenu = new LoadSaveMenu();

    }

    @Override
    public void show() {
        loadSaveMenu.setVisible(true);
    }


    @Override
    public void resize(int width, int height) {
        Strafer.uiManager.getViewport().update(width, height, true);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Strafer.uiManager.act(delta);
        Strafer.uiManager.draw();
    }

    @Override
    public void dispose() {
        Strafer.uiManager.dispose();
    }

    /**
     * changed to another screen
     */
    @Override
    public void hide() {
       loadSaveMenu.setVisible(false);
    }


    public static LoadSaveScreen getInstance() {
        if (instance == null) {
            instance = new LoadSaveScreen();
        }
        return instance;
    }
}
