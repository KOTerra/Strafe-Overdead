package com.straferdeliberator.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
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

		VisUI.load(SkinScale.X2);

		stage = new Stage(new ScreenViewport());

		root.setFillParent(true);
		root.pad(150);
		root.defaults().space(20);
		root.align(Align.right);
		stage.addActor(root);

		makeButtons();

		Gdx.input.setInputProcessor(stage);

	}

	private void makeButtons() {
		VisTextButton loadGameButton = new VisTextButton("ia si joaca");
		root.row();
		root.add(loadGameButton);
		loadGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showLoadGameMenu();
			}
		});

		VisTextButton optionsButton = new VisTextButton("Optiones");
		root.row();
		root.add(optionsButton);
		optionsButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showSettingsMenu();
			}
		});

		VisTextButton quitButton = new VisTextButton("Afara");
		root.row();
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

	/**
	 * changed to another screen
	 */
	@Override
	public void hide() {
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

	}

}
