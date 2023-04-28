package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;
import com.strafergame.ui.HUD;

public class GameScreen implements Screen {

	/**
	 * reference to the game class
	 */
	private final Strafer game;

	private GameWorld gameWorld;

	private HUD hud;

	public GameScreen(final Strafer game) {
		this.game = game;
		gameWorld = new GameWorld(game);
		hud = new HUD();
	}

	public void update(float delta) {	
		Strafer.worldCamera.update();
		Strafer.extendViewport.apply();

		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);
		Strafer.tiledMapRenderer.setView(Strafer.worldCamera);

		Strafer.uiScreenViewport.apply();

		Strafer.uiManager.act(delta);

		Strafer.updateStateTime();

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		update(delta);
		Strafer.tiledMapRenderer.render();

		gameWorld.update(delta);

		Strafer.uiManager.draw();

		gameWorld.getBox2DWorld().render();

	}

	@Override
	public void resize(int width, int height) {
		Strafer.extendViewport.update(width, height);
		Strafer.uiScreenViewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		Strafer.uiManager.dispose();
		gameWorld.dispose();
	}

	@Override
	public void hide() {
		hud.setVisible(false);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void show() {

		hud.setVisible(true);
	}

}
