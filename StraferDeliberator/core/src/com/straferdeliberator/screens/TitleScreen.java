package com.straferdeliberator.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.straferdeliberator.Strafer;

public class TitleScreen implements Screen {

	private Stage stage;
	private Strafer game;
	final Table root = new Table();

	public TitleScreen(Strafer game) {
		this.game = game;

		VisUI.load(SkinScale.X1);

		stage = new Stage(new ScreenViewport());

		root.setFillParent(true);
		stage.addActor(root);

		makeButtons();

		Gdx.input.setInputProcessor(stage);

	}

	private void makeButtons() {
		VisTextButton loadGameButton = new VisTextButton("ia si joaca");
		root.add(loadGameButton);
		loadGameButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// Dialogs.showOKDialog(stage, "VisUI demo", "Everything is OK!");
				showLoadGameMenu();
			}
		});

		VisTextButton optionsButton = new VisTextButton("Optiones");
		root.add(optionsButton);
		optionsButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSettingsMenu();
			}
		});

		VisTextButton quitButton = new VisTextButton("Afara");
		root.add(quitButton);
		quitButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});

	}

	private void showLoadGameMenu() {
		game.setScreen(new GameScreen(game));
	}

	private void showSettingsMenu() {
		
		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

	}

	@Override
	public void render(float a) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

}
