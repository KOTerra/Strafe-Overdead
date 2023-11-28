package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.strafergame.game.GameStateType;
import com.strafergame.Strafer;
import com.strafergame.assets.AssetUtils;
import com.strafergame.assets.AnimationProvider;

public class LoadingScreen implements Screen {

    private static LoadingScreen instance;

    /**
     * the game class
     */
    private final Strafer game;

    /**
     * renderer for the loading bar
     */
    private ShapeRenderer shapeRenderer;

    /**
     * progress of assets loading
     */
    private float progress;

    public LoadingScreen() {
        this.game = Strafer.getInstance();
        shapeRenderer = new ShapeRenderer();

        queueAssetsToLoad();
    }

    @Override
    public void show() {
    }

    /**
     * increases the progress and changes the screend when all loaded
     */
    private void update(float delta) {

        progress = MathUtils.lerp(progress, Strafer.assetManager.getProgress(), .1f);

        if (Strafer.assetManager.update()) {
            if (progress >= Strafer.assetManager.getProgress() - .001f) {
                if (Strafer.assetManager.isFinished()) {
                    AnimationProvider.prepareAnimations();

                    game.setScreen(TitleScreen.getInstance());
                }
            }
        }

    }

    @Override
    public void render(float delta) {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("#4a496b"));
        float height = (Gdx.graphics.getHeight() / 1080f) * 360f;
        shapeRenderer.rect(0, Gdx.graphics.getHeight() / 2f - height / 2f, Gdx.graphics.getWidth() * progress, height);
        shapeRenderer.end();

    }

    private void queueAssetsToLoad() {

        Strafer.assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
        for (String file : AssetUtils.listFilesInAssets("images", "png")) {
            Strafer.assetManager.load(file, Texture.class);
        }
        for (String file : AssetUtils.listFilesInAssets("ui/textures", "png")) {
            Strafer.assetManager.load(file, Texture.class);
        }

        Strafer.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        for (String file : AssetUtils.listFilesInAssets("maps", "tmx")) {
            Strafer.assetManager.load(file, TiledMap.class);
        }

        Strafer.assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
        for (String file : AssetUtils.listFilesInAssets("spritesheets", "atlas")) {

            Strafer.assetManager.load(file, TextureAtlas.class);

        }

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
        shapeRenderer.dispose();
    }

    public static LoadingScreen getInstance() {
        if (instance == null) {
            instance = new LoadingScreen();
        }
        return instance;
    }

}
