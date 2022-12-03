package com.straferdeliberator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;
import com.straferdeliberator.game.entity.player.Player;

public class GameScreen implements Screen {

	Strafer game;

	World world;

	private Sprite background;
	private Entity playerTest;

	public GameScreen(Strafer game) {
		addTestAssets();
		System.out.println("gamescreen");
	}

	void addTestAssets() {
		BodyDef body = new BodyDef();
		body.type = BodyType.DynamicBody;

		background = new Sprite(Strafer.assetManager.get("assets/back.png", Texture.class));

		background.setPosition(0, 0);
		background.setSize(background.getWidth() * Strafer.SCALE_FACTOR, background.getHeight() * Strafer.SCALE_FACTOR);

		playerTest = new Player();
		Strafer.stage.addActor(playerTest);
	}

	public void update(float delta) {
		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);

		Strafer.stage.act(delta);
	}

	@Override
	public void resize(int width, int height) {
		Strafer.extendViewport.update(width, height);
		Strafer.uiScreenViewport.update(width, height);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(1, 0, 0, 1);
		update(delta);

		Strafer.stage.draw();

		Strafer.spriteBatch.begin();
		background.draw(Strafer.spriteBatch);
		playerTest.getSprite().draw(Strafer.spriteBatch);
		Strafer.spriteBatch.end();

		Strafer.uiScreenViewport.apply();
		Strafer.uiCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.uiCamera.combined);

		Strafer.stage.act();
		Strafer.stage.draw();
	}

	@Override
	public void dispose() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {

	}

}
