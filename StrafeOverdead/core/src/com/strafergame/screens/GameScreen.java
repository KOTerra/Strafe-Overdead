package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;
import com.strafergame.ui.HUD;

public class GameScreen implements Screen {

    private static GameScreen instance;

    /**
     * reference to the game class
     */

    private GameWorld gameWorld;

    private HUD hud;

    public GameScreen() {
        gameWorld = new GameWorld();
        hud = new HUD();
        Strafer.uiManager.setHud(hud);

    }

    public void update(float delta) {
        Strafer.worldCamera.update();
        Strafer.extendViewport.apply();

        Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);
        Strafer.tiledMapRenderer.setView(Strafer.worldCamera);

        Strafer.uiScreenViewport.apply();

        Strafer.uiManager.act(delta);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        Strafer.tiledMapRenderer.render();

        gameWorld.update(delta);

        Strafer.uiManager.draw();

        gameWorld.getBox2DWorld().render();

    }

    public void showGameOverMenu() {

        Strafer.getInstance().setScreen(GameOverScreen.getInstance());
    }

    @Override
    public void resize(int width, int height) {
        Strafer.extendViewport.update(width, height);
        Strafer.uiScreenViewport.update(width, height, true);
        hud.resize();
    }

    @Override
    public void dispose() {
        Strafer.uiManager.dispose();
        gameWorld.dispose();
    }

    @Override
    public void hide() {
        hud.setVisible(false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void show() {

        hud.setVisible(true);
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public static GameScreen getInstance() {
        if (instance == null) {
            instance = new GameScreen();
        }
        return instance;
    }

}
