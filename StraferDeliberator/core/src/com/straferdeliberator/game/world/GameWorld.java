package com.straferdeliberator.game.world;

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
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.player.Player;
import com.straferdeliberator.game.world.collision.Box2DHelper;
import com.straferdeliberator.game.world.collision.Box2DWorld;

public class GameWorld extends Stage implements Disposable {

	private Sprite backgroundTest;
	private Player playerTest1;
	private Player playerTest2;
	private TiledMap tiledMapTest = Strafer.assetManager.get("maps/test/map.tmx", TiledMap.class);

	Box2DWorld box2DWorld = new Box2DWorld();

	public GameWorld() {
		super(Strafer.extendViewport, Strafer.spriteBatch);

		addTestAssets();
	}

	@Override
	public void act(float delta) {
		Strafer.updateStateTime(delta);

		box2DWorld.step(delta);

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
		box2DWorld.dispose();
	}

	void addTestAssets() {
		backgroundTest = new Sprite(Strafer.assetManager.get("images/back.png", Texture.class));
		backgroundTest.setPosition(0, 0);
		backgroundTest.setSize(backgroundTest.getWidth() * Strafer.SCALE_FACTOR,
				backgroundTest.getHeight() * Strafer.SCALE_FACTOR);

		playerTest1 = new Player();
		this.addActor(playerTest1);
		playerTest1.setPosition(Strafer.WORLD_WIDTH / 2, Strafer.WORLD_HEIGHT / 2);
		playerTest2 = new Player();
		this.addActor(playerTest2);
		playerTest2.setPosition(0, 0);
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
		}
	}

	public Box2DWorld getBox2DWorld() {
		return box2DWorld;
	}

}
