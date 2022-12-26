package com.straferdeliberator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.straferdeliberator.game.world.GameWorld;
import com.straferdeliberator.screens.LoadingScreen;

/**
 * The game class
 * 
 * @author mihai_stoica
 */
public class Strafer extends Game {

	/**
	 * the asset manager
	 */
	public static final AssetManager assetManager = new AssetManager(new InternalFileHandleResolver());

	/**
	 * the sprite batch
	 */
	public static SpriteBatch spriteBatch;

	/**
	 * the time elapsed. used to retrieve time dependent data such as animation
	 * frames
	 */
	private static float stateTime = 0f;

	/**
	 * the world width measured in tiles. a tile is 64x64 pixels
	 */
	public static final float WORLD_WIDTH = 30;

	/**
	 * the world height measured in tiles. a tile is 64x64 pixels
	 */
	public static final float WORLD_HEIGHT = 16.875f;

	/**
	 * used to scale from pixel units to world units
	 */
	public static final float SCALE_FACTOR = 1 / 64f;

	/**
	 * the aspect ratio of the window
	 */
	public static float aspectRatio;

	/**
	 * camera used for rendering tiles and entities. it uses world units
	 */
	public static OrthographicCamera worldCamera;

	/**
	 * camera used for rendering user interface components. it uses pixel units
	 */
	public static OrthographicCamera uiCamera;

	/**
	 * viewport used by the worldCamera
	 */
	public static ExtendViewport extendViewport;

	/**
	 * viewport used for the user interface components
	 */
	public static ScreenViewport uiScreenViewport;

	/**
	 * stage that contains world entities
	 */
	public static GameWorld gameWorld;

	/**
	 * the tiled map
	 */
	public static TiledMap tiledMap;

	/**
	 * the map renderer
	 */
	public static OrthogonalTiledMapRenderer tiledMapRenderer;

	/**
	 * stage that contains ui components
	 */
	public static Stage uiStage;

	/**
	 * whether game is in debug mode
	 */
	public static boolean inDebug = true;

	@Override
	public void create() {
		spriteBatch = new SpriteBatch();
		aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

		worldCamera = new OrthographicCamera(WORLD_HEIGHT * aspectRatio, WORLD_HEIGHT);
		worldCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		extendViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCamera);

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, SCALE_FACTOR, spriteBatch);
		tiledMapRenderer.setView(worldCamera);

		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
		uiScreenViewport = new ScreenViewport(uiCamera);

		uiStage = new Stage(uiScreenViewport, spriteBatch);

		setScreen(new LoadingScreen(this));

	}

	@Override
	public void dispose() {
		assetManager.dispose();
		spriteBatch.dispose();
		tiledMapRenderer.dispose();
		this.getScreen().dispose();
	}

	public static float getStateTime() {
		return stateTime;
	}

	public static void setStateTime(float stateTime) {
		Strafer.stateTime = stateTime;
	}

	public static void updateStateTime(float amount) {
		Strafer.setStateTime(Strafer.getStateTime() + amount);
		if (Strafer.getStateTime() > 10000000f) {
			Strafer.setStateTime(0);
		}
	}

}
