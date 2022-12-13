package com.straferdeliberator.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.world.GameWorld;

public class GameScreen implements Screen {

	Strafer game;

	World world;

	public GameScreen(Strafer game) {
		Strafer.gameWorld = new GameWorld();

	}

	public void update(float delta) {
		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);

		Strafer.gameWorld.act(delta);
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

		Strafer.spriteBatch.begin();

		Strafer.gameWorld.act();
		Strafer.gameWorld.draw();

		Strafer.spriteBatch.end();

		Strafer.uiScreenViewport.apply();
		Strafer.uiCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.uiCamera.combined);

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
