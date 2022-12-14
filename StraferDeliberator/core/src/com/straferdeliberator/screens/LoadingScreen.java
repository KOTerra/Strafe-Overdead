package com.straferdeliberator.screens;

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
import com.straferdeliberator.Strafer;

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
		shapeRenderer = new ShapeRenderer();

		queueAssetsToLoad();
		System.out.println("LoadingScreen.LoadingScreen()");
	}

	@Override
	public void show() {
	}

	/**
	 * mareste loadingBarul in functie de cat de mult s-a incarcat din asset-uri
	 */
	private void update(float delta) {

		progress = MathUtils.lerp(progress, Strafer.assetManager.getProgress(), .1f);

		if (Strafer.assetManager.update()) {
			if (progress >= Strafer.assetManager.getProgress() - .001f) {
				System.out.println("gata");
				if (Strafer.assetManager.isFinished()) {
					game.setScreen(new GameScreen(game));
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
		shapeRenderer.setColor(Color.valueOf("#40444b"));
		shapeRenderer.rect(0, Gdx.graphics.getHeight() / 2f - 435 / 2f, Gdx.graphics.getWidth() * progress, 435);
		shapeRenderer.end();

	}

	private void queueAssetsToLoad() {
		// should load automatically based on folder structure
		// if in folder sprites load as atlas animation etc
		Strafer.assetManager.setLoader(Texture.class, new TextureLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("images/pep.png", Texture.class);
		Strafer.assetManager.load("images/back.png", Texture.class);

		Strafer.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("maps/test/map.tmx", TiledMap.class);

		Strafer.assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));
		Strafer.assetManager.load("spritesheets/player/player-idle.atlas", TextureAtlas.class);
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
