package com.strafergame.game.world;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.strafergame.Strafer;
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.player.Player;
import com.strafergame.game.world.collision.Box2DHelper;
import com.strafergame.game.world.collision.Box2DWorld;

public class GameWorld extends Stage implements Disposable {

	private Sprite backgroundTest;
	private Player playerTest1;
	private Player playerTest2;
	private final TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/map.tmx", TiledMap.class);

	private float alpha = 0.25f;

	Strafer game;

	/**
	 * 
	 */

	Box2DWorld box2DWorld = new Box2DWorld();
//	EntityEngine entityEngine = new EntityEngine();

	public GameWorld(Strafer game) {
		super(Strafer.extendViewport, Strafer.spriteBatch);
		this.game = game;

		addTestAssets();
	}

	public void savePositions() {

		for (Actor a : this.getActors()) {
			((Entity) a).savePosition();
		}
	}

	@Override
	public void act(float delta) {

		for (Actor a : this.getActors()) {
			a.act(delta);
		}

		debugControls();

	}

	@Override
	public void draw() {
		// backgroundTest.draw(Strafer.spriteBatch);

		for (Actor a : this.getActors()) {
			a.draw(getBatch(), 1);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		box2DWorld.dispose();
	}

	void addTestAssets() {
		backgroundTest = new Sprite(Strafer.assetManager.get("images/back.png", Texture.class));
		backgroundTest.setPosition(0, 0);
		backgroundTest.setSize(backgroundTest.getWidth() * Strafer.SCALE_FACTOR,
				backgroundTest.getHeight() * Strafer.SCALE_FACTOR);

		playerTest1 = new Player();
		playerTest1.setGameWorld(this);
		this.addActor(playerTest1);

		playerTest2 = new Player();
		playerTest2.setGameWorld(this);
		this.addActor(playerTest2);
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
				playerTest1.getBody().setTransform(playerTest1.getX() + .5f, playerTest1.getY() + .5f, 0);
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
