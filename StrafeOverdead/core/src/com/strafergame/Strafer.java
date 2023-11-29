package com.strafergame;

import java.util.Locale;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.graphics.OrthogonalTiledMapRendererBleeding;
import com.strafergame.graphics.WorldCamera;
import com.strafergame.input.InputManager;
import com.strafergame.screens.*;
import com.strafergame.settings.Settings;
import com.strafergame.ui.UiManager;

/**
 * The game class
 *
 * @author mihai_stoica
 */
public class Strafer extends Game {

    private static Strafer instance;
    /**
     * the asset manager
     */
    public static final AssetManager assetManager = new AssetManager(new InternalFileHandleResolver());

    public static I18NBundle i18n;

    /**
     * input manager of the game
     */
    public static InputManager inputManager;

    /**
     * the sprite batch
     */
    public static SpriteBatch spriteBatch;

    /**
     * the world width measured in tiles.
     */
    public static final float WORLD_WIDTH = 30;

    /**
     * the world height measured in tiles.
     */
    public static final float WORLD_HEIGHT = 16.875f;

    /**
     * used to scale from pixel units to world units. 1x1m tile=32x32pixels
     */
    public static final float SCALE_FACTOR = 1 / 32f;

    /**
     * the aspect ratio of the window
     */
    public static float aspectRatio;

    /**
     * camera used to follow entities. it uses world units
     */
    public static WorldCamera worldCamera;

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
     * stage that contains ui components
     */
    public static UiManager uiManager;

    /**
     * the tiled map
     */
    public static TiledMap tiledMap;

    /**
     * the map renderer
     */
    public static OrthogonalTiledMapRendererBleeding tiledMapRenderer;

    public static GameRenderer gameRenderer;

    /**
     * whether game is in debug mode
     */
    public static boolean inDebug = true;

    public static Strafer getInstance() {
        if (instance == null) {
            instance = new Strafer();
        }
        return instance;
    }

    @Override
    public void create() {

        i18n = I18NBundle.createBundle(Gdx.files.internal("i18n/ui/bundle"),
                new Locale(Settings.getPreferences().getString("LANGUAGE")), "utf-8");

        spriteBatch = new SpriteBatch();

        inputManager = new InputManager();

        aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        worldCamera = new WorldCamera(WORLD_HEIGHT * aspectRatio, WORLD_HEIGHT);
        worldCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        extendViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCamera);
        tiledMapRenderer = new OrthogonalTiledMapRendererBleeding(tiledMap, SCALE_FACTOR, spriteBatch);

        tiledMapRenderer.setView(worldCamera);

        uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.position.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
        uiScreenViewport = new ScreenViewport(uiCamera);

        uiManager = new UiManager(uiScreenViewport, spriteBatch);
        uiManager.init();

        gameRenderer = new GameRenderer();

        setScreen(LoadingScreen.getInstance());
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        spriteBatch.dispose();
        tiledMapRenderer.dispose();
        this.getScreen().dispose();
    }

}
