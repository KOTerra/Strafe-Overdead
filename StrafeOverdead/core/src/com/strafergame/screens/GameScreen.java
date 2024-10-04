package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;
import com.strafergame.ui.HUD;

import java.util.ArrayList;
import java.util.zip.Deflater;

public class GameScreen implements Screen {

    private static GameScreen instance;

    /**
     * reference to the game class
     */

    private GameWorld gameWorld;

    private HUD hud;

    private VfxManager vfxManager;
    private ArrayList<VfxEffect> shaderEffects = new ArrayList<>();
    ChromaticAberrationEffect chromaticAberrationEffect;
    BloomEffect bloomEffect;
    OldTvEffect tvEffect;

    private static boolean takeScreenshot = false;
    private static String screenshotPath;

    public GameScreen() {
        gameWorld = new GameWorld();
        hud = new HUD();
        Strafer.uiManager.setHud(hud);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);


        chromaticAberrationEffect = new ChromaticAberrationEffect(4);
        chromaticAberrationEffect.setMaxDistortion(.25f);
        tvEffect = new OldTvEffect();
        tvEffect.setTime(.15f);
        bloomEffect = new BloomEffect();
        bloomEffect.setBloomIntensity(1.2f);

        //addShaderEffect(chromaticAberrationEffect);
//        addShaderEffect(bloomEffect);
//        addShaderEffect(tvEffect);
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

        if (takeScreenshot) {
            Pixmap originalPixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

            int newWidth = 240;
            int newHeight = 135;
            Pixmap resizedPixmap = new Pixmap(newWidth, newHeight, originalPixmap.getFormat());
            resizedPixmap.drawPixmap(
                    originalPixmap,  // The original Pixmap to be scaled
                    0, 0,           // Source position (x, y) from the original pixmap
                    originalPixmap.getWidth(), originalPixmap.getHeight(),  // Source width and height
                    0, 0,           // Destination position (x, y) on the resized pixmap
                    newWidth, newHeight  // Destination width and height (new size)
            );

            PixmapIO.writePNG(Gdx.files.external(screenshotPath), resizedPixmap, Deflater.NO_COMPRESSION,true);

            originalPixmap.dispose();
            resizedPixmap.dispose();
//            PixmapIO.writePNG(Gdx.files.external(screenshotPath), Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight()), Deflater.DEFAULT_COMPRESSION, true);
            takeScreenshot = false;
        }

    }

    public void showGameOverMenu() {

        Strafer.getInstance().setScreen(GameOverScreen.getInstance());
    }

    @Override
    public void resize(int width, int height) {
        Strafer.extendViewport.update(width, height);
        Strafer.uiScreenViewport.update(width, height, true);
        hud.resize();

        vfxManager.resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    @Override
    public void dispose() {
        Strafer.uiManager.dispose();
        gameWorld.dispose();
        vfxManager.dispose();
        for (VfxEffect e : shaderEffects) {
            e.dispose();
        }
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

    public ArrayList<VfxEffect> getShaderEffects() {
        return shaderEffects;
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }

    public void addShaderEffect(ChainVfxEffect effect) {
        vfxManager.addEffect(effect);
        shaderEffects.add(effect);
    }

    public void removeShaderEffect(ChainVfxEffect effect) {
        vfxManager.removeEffect(effect);
        shaderEffects.remove(effect);
    }

    public static void scheduleScreenshot(String path) {
        screenshotPath = path;
        takeScreenshot = true;
    }

    public static GameScreen getInstance() {
        if (instance == null) {
            instance = new GameScreen();
        }
        return instance;
    }

}
