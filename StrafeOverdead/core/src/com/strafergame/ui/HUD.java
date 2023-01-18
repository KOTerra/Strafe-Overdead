package com.strafergame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;

public class HUD {
	Table root = new Table();

	public HUD() {

		mockUI();
	}

	private void mockUI() {

		root.setFillParent(true);
		root.pad(150);
		root.defaults().space(20);
		root.align(Align.bottom);
		Strafer.uiManager.addActor(root);

		VisTextButton mockButton = new VisTextButton("hud");
		root.row();
		root.add(mockButton);
		mockButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				System.out.println("ai hud bos");
			}
		});

	}

	public void hide() {
		root.setVisible(!root.isVisible());
	}

}
