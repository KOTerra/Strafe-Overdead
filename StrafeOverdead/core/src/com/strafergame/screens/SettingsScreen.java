package com.strafergame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.SettingsMenu;

public class SettingsScreen implements Screen {

	SettingsMenu settingsMenu;

	public SettingsScreen(Strafer game) {
		settingsMenu = new SettingsMenu(game);
	}

	@Override
	public void resize(int width, int height) {
		Strafer.uiManager.getViewport().update(width, height, true);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Strafer.uiManager.act(delta);
		Strafer.uiManager.draw();
	}

	@Override
	public void dispose() {
		Strafer.uiManager.dispose();
	}

	/**
	 * changed to another screen
	 */
	@Override
	public void hide() {
		settingsMenu.setVisible(false);
	}

	/**
	 * app out of focus or closed
	 */
	@Override
	public void pause() {

	}

	/**
	 * app returned to focus
	 */
	@Override
	public void resume() {
	}

	/**
	 * changed to this screen
	 */
	@Override
	public void show() {

		settingsMenu.setVisible(true);
	}

}
