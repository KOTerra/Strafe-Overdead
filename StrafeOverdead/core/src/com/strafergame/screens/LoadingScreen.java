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
import com.strafergame.GameState;
import com.strafergame.Strafer;
import com.strafergame.graphics.AnimationProvider;

public class LoadingScreen implements Screen {

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

	public LoadingScreen(final Strafer game) {
		this.game = game;
		Strafer.gameState = GameState.LOADING;
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
					// game.setScreen(new GameScreen(game));
					if (Strafer.titleScreen == null) {
						Strafer.titleScreen = new TitleScreen(game);
					}
					game.setScreen(Strafer.titleScreen);
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
		shapeRenderer.setColor(Color.valueOf("#4F526b"));
		float height = (Gdx.graphics.getHeight() / 1080f) * 360f;
		shapeRenderer.rect(0, Gdx.graphics.getHeight() / 2f - height / 2f, Gdx.graphics.getWidth() * progress, height);
		shapeRenderer.end();

	}

	private void queueAssetsToLoad() {
		// should load automatically based on folder structure
		// if in folder sprites load as atlas animation etc
		Strafer.assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("images/pep.png", Texture.class);
		Strafer.assetManager.load("images/back.png", Texture.class);
		Strafer.assetManager.load("ui/backgrounds/banner.png", Texture.class);

		Strafer.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("maps/test/test.tmx", TiledMap.class);

		Strafer.assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("spritesheets/player/player.atlas", TextureAtlas.class);

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

}
