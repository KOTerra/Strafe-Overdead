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

	GameWorld gameWorld;

	public final static float FIXED_TIME_STEP = 1 / 45f;
	private float accumulator = 0f;
	float alpha = 0.25f;

	private HUD hud;

	public GameScreen(final Strafer game) {
		this.game = game;
		gameWorld = new GameWorld(game);
		hud = new HUD();
	}

	public void update(float delta) {
		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
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

		float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
		accumulator += frameTime;
		while (accumulator >= FIXED_TIME_STEP) {
			gameWorld.getBox2DWorld().step(FIXED_TIME_STEP);
			accumulator -= FIXED_TIME_STEP;
		}
		alpha = accumulator / FIXED_TIME_STEP;

		gameWorld.act(alpha);

		Strafer.spriteBatch.begin();
		gameWorld.draw();
		Strafer.spriteBatch.end();

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
