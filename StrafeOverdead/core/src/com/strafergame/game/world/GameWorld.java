package com.strafergame.game.world;

import java.util.Locale;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DWorld;

import box2dLight.RayHandler;

public class GameWorld implements Disposable {

	private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/test.tmx", TiledMap.class);

	public static final float FIXED_TIME_STEP = 1 / 90f;

	Strafer game;

	/**
	 * 
	 */

	private final Box2DWorld box2DWorld = new Box2DWorld();
	private final RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());
	private final EntityEngine entityEngine;
	private Entity dummy;
	private Entity dummy2;

	public static Entity player;

	public GameWorld(Strafer game) {
		this.game = game;
		entityEngine = new EntityEngine(game, box2DWorld, rayHandler);
		player = entityEngine.createPlayer(new Vector2(0, 0));

		addTestAssets();
	}

	public void update(float delta) {
		entityEngine.update(delta);
		debugUpdate();

	}

	@Override
	public void dispose() {
		entityEngine.dispose();
	}

	void addTestAssets() {

		dummy = entityEngine.createDummy(new Vector2(10, 5), 3);
		dummy2 = entityEngine.createDummy(new Vector2(10, 5), 1);
		entityEngine.createHitboxDummy(new Vector2(15, 5), null);

		Strafer.worldCamera.setFocusOn(player);

		Strafer.tiledMapRenderer.setMap(tiledMapTest);

		TiledMapTileLayer walls = (TiledMapTileLayer) tiledMapTest.getLayers().get("walls");
		for (int i = 1; i <= walls.getWidth(); i++) {
			for (int j = 1; j <= walls.getHeight(); j++) {
				if (walls.getCell(i, j) != null) {
					Box2DFactory.createWall(box2DWorld.getWorld(), 1, 1, new Vector3(i, j, 0));
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
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_2)) {

				Strafer.worldCamera.setFocusBetween(true, player, dummy, dummy2);
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {

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
