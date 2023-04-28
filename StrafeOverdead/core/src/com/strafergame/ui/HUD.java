package com.strafergame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.strafergame.Strafer;
import com.strafergame.input.PlayerControl;

public class HUD extends Table {

	public HUD() {
		mockUI();
	}

	private void mockUI() {

		setFillParent(true);
		pad(150);
		defaults().space(20);
		align(Align.bottomLeft);
		Strafer.uiManager.getRoot().addActor(this);

		/*
		 * VisTextButton mockButton = new VisTextButton(Strafer.i18n.get("hud")); row();
		 * add(mockButton); mockButton.addListener(new ChangeListener() {
		 * 
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * System.out.println("ai hud bos"); hide(); } }); pack();
		 */
		final float deadzone = 5f;
		final Touchpad touchpad = new Touchpad(deadzone, VisUI.getSkin());
		touchpad.setScale(15);
		touchpad.setOrigin(Align.center);
		touchpad.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				PlayerControl.MOVE_UP = touchpad.getKnobY() > deadzone;
				PlayerControl.MOVE_LEFT = touchpad.getKnobX() < -deadzone;
				PlayerControl.MOVE_DOWN = touchpad.getKnobY() < -deadzone;
				PlayerControl.MOVE_RIGHT = touchpad.getKnobX() > deadzone;

			}
		});
		add(touchpad);
	}

	public void hide() {
		setVisible(!isVisible());
	}

}
