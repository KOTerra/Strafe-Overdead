package com.straferdeliberator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.straferdeliberator.Strafer;

public class GameScreen implements Screen {

	Strafer game;

	World world;

	private Sprite background;
	private Sprite sprite;

	public GameScreen(Strafer game) {
		addTestAssets();
		System.out.println("gamescreen");
	}

	void addTestAssets() {
		BodyDef body = new BodyDef();
		body.type = BodyType.DynamicBody;

		background = new Sprite(Strafer.assetManager.get("assets/back.png", Texture.class));
		sprite = new Sprite(Strafer.assetManager.get("assets/pep.png", Texture.class));
		sprite.setPosition(Strafer.WORLD_WIDTH / 2 - sprite.getWidth() * Strafer.SCALE_FACTOR / 2,
				Strafer.WORLD_HEIGHT / 2 - sprite.getHeight() * Strafer.SCALE_FACTOR / 2);
		background.setPosition(0, 0);
		background.setSize(background.getWidth() * Strafer.SCALE_FACTOR, background.getHeight() * Strafer.SCALE_FACTOR);
		sprite.setSize(sprite.getWidth() * Strafer.SCALE_FACTOR, sprite.getHeight() * Strafer.SCALE_FACTOR);

	}

	@Override
	public void resize(int width, int height) {
		Strafer.extendViewport.update(width, height);
		Strafer.uiScreenViewport.update(width, height);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float arg0) {
		ScreenUtils.clear(1, 0, 0, 1);

		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);

		Strafer.stage.act();
		Strafer.stage.draw();

		Strafer.spriteBatch.begin();
		background.draw(Strafer.spriteBatch);
		sprite.draw(Strafer.spriteBatch);
		Strafer.spriteBatch.end();

		Strafer.uiScreenViewport.apply();
		Strafer.uiCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.uiCamera.combined);

		Strafer.stage.act();
		Strafer.stage.draw();
	}

}
