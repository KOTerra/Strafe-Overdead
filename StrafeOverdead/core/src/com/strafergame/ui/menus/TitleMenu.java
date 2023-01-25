package com.strafergame.ui.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.screens.GameScreen;

public class TitleMenu extends Table {
	Strafer game;

	public TitleMenu(Strafer game) {
		this.game = game;
		setFillParent(true);
		pad(150);
		defaults().space(20);
		align(Align.right);
		Strafer.uiManager.addActor(this);

		makeButtons();
	}

	private void makeButtons() {
		VisTextButton loadGameButton = new VisTextButton(Strafer.i18n.get("playButton"));
		loadGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showLoadGameMenu();
			}
		});
		row();
		add(loadGameButton);

		VisTextButton optionsButton = new VisTextButton("O");
		optionsButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSettingsMenu();
			}
		});
		row();
		add(optionsButton);

		VisTextButton quitButton = new VisTextButton("Afara");
		quitButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});
		row();
		add(quitButton);

	}

	private void showLoadGameMenu() {
		if (Strafer.gameScreen == null) {
			Strafer.gameScreen = new GameScreen(game);
		}
		game.setScreen(Strafer.gameScreen);
	}

	private void showSettingsMenu() {

	}
}
