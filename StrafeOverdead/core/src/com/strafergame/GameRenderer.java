package com.strafergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameRenderer implements Disposable {

	public GameRenderer() {

	}

	public void update(float delta) {
		Strafer.extendViewport.apply();
		Strafer.worldCamera.update();
		Strafer.spriteBatch.setProjectionMatrix(Strafer.worldCamera.combined);
		Strafer.tiledMapRenderer.setView(Strafer.worldCamera);

		Strafer.uiScreenViewport.apply();

		Strafer.uiManager.act(delta);
		// gameWorld.act(delta);

		Strafer.updateStateTime();

	}

	public void render(float alpha) {
		ScreenUtils.clear(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void dispose() {

	}

}
