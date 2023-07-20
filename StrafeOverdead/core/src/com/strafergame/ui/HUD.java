package com.strafergame.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.strafergame.Strafer;
import com.strafergame.input.PlayerControl;

public class HUD extends Table {

	private VisProgressBar healthBar;
	public HUD() {
		setFillParent(true);
		pad(40);

		align(Align.center);
		Strafer.uiManager.addActor(this);
		this.healthBar= makeHealthBar();

		mockUI();

	}

	private VisProgressBar makeHealthBar(){
		VisProgressBar healthbar=new VisProgressBar(0,100,1,false);


		add(healthbar).expandX().top().left();
		row();
		add(new Table()).fillY().expandY();
		row();
		return healthbar;
	}
	private void mockUI() {
		align(Align.bottomLeft);
		/*
		 * VisTextButton mockButton = new VisTextButton(Strafer.i18n.get("hud")); row();
		 * add(mockButton); mockButton.addListener(new ChangeListener() {
		 * 
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * System.out.println("ai hud bos"); hide(); } }); pack();
		 */
		final float deadzone = 5f;
		final Touchpad touchpad = new Touchpad(deadzone, VisUI.getSkin());
		touchpad.setScale(20);
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
		add(touchpad).bottom().left().pad(60);
		//row();
	}
	public void resize() {
		setBounds(0, 0, Strafer.uiManager.getWidth(), Strafer.uiManager.getHeight());

	}

	public void hide() {
		setVisible(!isVisible());
	}

}
