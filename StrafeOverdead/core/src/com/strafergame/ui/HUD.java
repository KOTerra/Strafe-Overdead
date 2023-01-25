package com.strafergame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;

public class HUD extends Table {

	public HUD() {
		mockUI();
	}

	private void mockUI() {

		setFillParent(true);
		pad(150);
		defaults().space(20);
		align(Align.bottom);
		Strafer.uiManager.getRoot().addActor(this);

		VisTextButton mockButton = new VisTextButton(Strafer.i18n.get("hud"));
		row();
		add(mockButton);
		mockButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("ai hud bos");
				hide();
			}
		});
		pack();
	}

	public void hide() {
		setVisible(!isVisible());
	}

}
