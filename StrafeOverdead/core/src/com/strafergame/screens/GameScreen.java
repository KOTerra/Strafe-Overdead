package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;

public class GameScreen implements Screen {

	/**
	 * reference to the game class
	 */
	private final Strafer game;

	public GameScreen(final Strafer game) {
		this.game = game;
		Strafer.gameWorld = new GameWorld();

	}

	public void update(float delta) {
		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);
		Strafer.tiledMapRenderer.setView(Strafer.worldCamera);

		// Strafer.inputManager.processInput();
		Strafer.uiScreenViewport.apply();

		Strafer.uiManager.act(delta);
		Strafer.gameWorld.act(delta);
	}

	@Override
	public void render(float delta) {
		update(delta);

		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Strafer.tiledMapRenderer.render();

		Strafer.spriteBatch.begin();
		Strafer.gameWorld.draw();
		Strafer.spriteBatch.end();

		Strafer.gameWorld.getBox2DWorld().render();

		Strafer.uiManager.draw();

	}

	@Override
	public void resize(int width, int height) {
		Strafer.extendViewport.update(width, height);
		Strafer.uiScreenViewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		Strafer.uiManager.dispose();
		Strafer.gameWorld.dispose();
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
