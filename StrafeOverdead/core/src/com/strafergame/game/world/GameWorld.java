package com.strafergame.game.world;

import java.util.Locale;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.world.collision.Box2DHelper;
import com.strafergame.game.world.collision.Box2DWorld;

import box2dLight.RayHandler;

public class GameWorld implements Disposable {

	private Sprite backgroundTest;
	private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/map.tmx", TiledMap.class);

	public static final float FIXED_TIME_STEP = 1 / 45f;

	Strafer game;

	/**
	 * 
	 */
	private final Box2DWorld box2DWorld = new Box2DWorld();
	private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());
	private final EntityEngine entityEngine = new EntityEngine(box2DWorld, rayHandler);

	private Entity player;

	public GameWorld(Strafer game) {
		this.game = game;

		player = entityEngine.createPlayer(new Vector2(0, 0));

		addTestAssets();
	}

	public void update(float delta) {
		entityEngine.update(delta);
		debugUpdate();

	}

	@Override
	public void dispose() {
		box2DWorld.dispose();
	}

	void addTestAssets() {
		backgroundTest = new Sprite(Strafer.assetManager.get("images/back.png", Texture.class));
		backgroundTest.setPosition(0, 0);
		backgroundTest.setSize(backgroundTest.getWidth() * Strafer.SCALE_FACTOR,
				backgroundTest.getHeight() * Strafer.SCALE_FACTOR);

		Strafer.worldCamera.setFocusOn(player);

		Strafer.tiledMapRenderer.setMap(tiledMapTest);

		TiledMapTileLayer walls = (TiledMapTileLayer) tiledMapTest.getLayers().get("walls");
		for (int i = 1; i <= walls.getTileWidth(); i++) {
			for (int j = 1; j <= walls.getTileHeight(); j++) {
				if (walls.getCell(i, j) != null) {
					Box2DHelper.createWall(box2DWorld.getWorld(), 1, 1, new Vector3(i, j, 0));
				}
			}
		}
	}

	void debugControls() {
		if (Strafer.inDebug) {

			if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
				Strafer.worldCamera.zoom += .02f;
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
				Strafer.worldCamera.zoom -= .02f;
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_0)) {
				Strafer.worldCamera.removeFocus();
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_1)) {
				Strafer.worldCamera.setFocusOn(player);
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {

			}
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
				game.setScreen(Strafer.titleScreen);
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
				Strafer.i18n = I18NBundle.createBundle(Gdx.files.internal("assets/i18n/ui/bundle"), new Locale("ro"),
						"utf-8");
			}
		}
	}

	void debugUpdate() {

		debugControls();
	}

	public Box2DWorld getBox2DWorld() {
		return box2DWorld;
	}

}
