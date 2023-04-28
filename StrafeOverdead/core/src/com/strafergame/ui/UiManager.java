package com.strafergame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.strafergame.Strafer;

public class UiManager extends Stage implements Disposable {

	public UiManager(Viewport viewport, SpriteBatch spriteBatch) {
		super(viewport, spriteBatch);

	}

	public void init() {
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(Strafer.uiManager);

		VisUI.load(SkinScale.X2);

	}

	@Override
	public void act(float delta) {
		super.act(delta);

	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
