package com.strafergame.game.world;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.player.Player;
import com.strafergame.game.world.collision.Box2DHelper;
import com.strafergame.game.world.collision.Box2DWorld;

import box2dLight.RayHandler;

public class GameWorld implements Disposable {

	private Sprite backgroundTest;
	private Player playerTest1;
	private Player playerTest2;
	private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/map.tmx", TiledMap.class);

	private Array<Entity> entities;

	public final static float FIXED_TIME_STEP = 1 / 45f;
	private float accumulator = 0f;
	float alpha = 0.25f;

	Strafer game;

	/**
	 * 
	 */

	Box2DWorld box2DWorld = new Box2DWorld();
	RayHandler rayHandler = new RayHandler(box2DWorld.getWorld());
	EntityEngine entityEngine = new EntityEngine(box2DWorld, rayHandler);

	public GameWorld(Strafer game) {
		this.game = game;
		this.entities = new Array<>();

		entityEngine.makePlayer(new Vector2(0, 0));

		addTestAssets();
	}

	public void update(float delta) {
		entityEngine.update(delta);
		debugUpdate();

	}

	public void draw() {
		// backgroundTest.draw(Strafer.spriteBatch);

		for (Entity e : this.getEntities()) {
			e.draw(Strafer.spriteBatch);
		}
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

		playerTest1 = new Player();
		playerTest1.setGameWorld(this);
	//	this.addEntity(playerTest1);

		playerTest2 = new Player();
		playerTest2.setGameWorld(this);
	//	this.addEntity(playerTest2);
	Strafer.worldCamera.setFocusOn(playerTest1);

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

			if (Gdx.input.isKeyPressed(Keys.NUMPAD_1)) {
				Strafer.worldCamera.setFocusOn(playerTest1);
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_2)) {
				Strafer.worldCamera.setFocusOn(playerTest2);
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_0)) {
				Strafer.worldCamera.removeFocus();
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {
				// playerTest1.getBody().setTransform(playerTest1.getX() + .5f,
				// playerTest1.getY() + .5f, 0);
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
		act();
		float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
		accumulator += frameTime;
		while (accumulator >= FIXED_TIME_STEP) {
			savePositions();
			accumulator -= FIXED_TIME_STEP;
			box2DWorld.step(FIXED_TIME_STEP);

		}

		alpha = accumulator / FIXED_TIME_STEP;

		debugControls();
	}

	private void addEntity(Entity entity) {
		entities.add(entity);
	}

	public void savePositions() {
		for (Entity e : this.getEntities()) {
			e.savePosition();
		}
	}

	private void act() {
		for (Entity e : this.getEntities()) {
			e.act();
		}
	}

	private Array<Entity> getEntities() {
		return entities;
	}

	public Box2DWorld getBox2DWorld() {
		return box2DWorld;
	}

	public float getInterPolationAlpha() {
		return alpha;
	}

	public void setInterpolationAlpha(float alpha) {
		this.alpha = alpha;
	}

}
