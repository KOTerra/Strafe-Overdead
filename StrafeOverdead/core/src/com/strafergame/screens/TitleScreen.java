package com.strafergame.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.TitleMenu;

public class TitleScreen implements Screen {
	TitleMenu titleMenu;

	public TitleScreen(Strafer game) {
		titleMenu = new TitleMenu(game);

	}

	@Override
	public void resize(int width, int height) {
		Strafer.uiManager.getViewport().update(width, height, true);
		titleMenu.resize();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderBackground();
		Strafer.uiManager.act(delta);
		Strafer.uiManager.draw();
		Strafer.uiManager.setDebugAll(Strafer.inDebug);

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
		titleMenu.setVisible(false);
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
		if (Gdx.app.getType().equals(ApplicationType.Desktop)) {

			Pixmap pixmap = new Pixmap(Gdx.files.internal("assets/ui/cursor.png"));
			// Set hotspot to the middle of it (0,0 would be the top-left corner)
			Cursor cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
			pixmap.dispose(); // We don't need the pixmap anymore
			Gdx.graphics.setCursor(cursor);
		}
		titleMenu.setVisible(true);
	}

	public void renderBackground() {

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.valueOf("#4F526b"));
		float height = (Gdx.graphics.getHeight() / 1080f) * 360f;
		shapeRenderer.rect(0, Gdx.graphics.getHeight() / 2f - height / 2f, Gdx.graphics.getWidth(), height);
		shapeRenderer.end();
	}
}
