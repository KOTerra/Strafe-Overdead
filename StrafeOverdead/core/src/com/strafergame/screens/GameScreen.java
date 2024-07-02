package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.ChromaticAberrationEffect;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;
import com.strafergame.ui.HUD;

import java.util.ArrayList;

public class GameScreen implements Screen {

    private static GameScreen instance;

    /**
     * reference to the game class
     */

    private GameWorld gameWorld;

    private HUD hud;

    private VfxManager vfxManager;
    private ChromaticAberrationEffect vfxEffect;
    private ArrayList<ShaderVfxEffect> shaderEffects;

    public GameScreen() {
        gameWorld = new GameWorld();
        hud = new HUD();
        Strafer.uiManager.setHud(hud);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxEffect = new ChromaticAberrationEffect(4);
        vfxEffect.setMaxDistortion(.25f);
        vfxManager.addEffect(vfxEffect);

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

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        update(delta);

        gameWorld.update(delta);

        Strafer.uiManager.draw();

        gameWorld.getBox2DWorld().render();

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

    }

    public void showGameOverMenu() {

        Strafer.getInstance().setScreen(GameOverScreen.getInstance());
    }

    @Override
    public void resize(int width, int height) {
        Strafer.extendViewport.update(width, height);
        Strafer.uiScreenViewport.update(width, height, true);
        hud.resize();

        vfxManager.resize(width * 2, height * 2);

    }

    @Override
    public void dispose() {
        Strafer.uiManager.dispose();
        gameWorld.dispose();
        vfxManager.dispose();
        vfxEffect.dispose();
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

    public ArrayList<ShaderVfxEffect> getShaderEffects() {
        return shaderEffects;
    }

    public static GameScreen getInstance() {
        if (instance == null) {
            instance = new GameScreen();
        }
        return instance;
    }

}
