package com.game.straferdeliberator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The game class
 * 
 * @author mihai_stoica
 */
public class StraferDeliberator extends ApplicationAdapter implements InputProcessor {

	/**
	 * the asset manager
	 */
	public static AssetManager assetManager;
	/**
	 * the sprite batch
	 */
	public static SpriteBatch spriteBatch;

	/**
	 * camera used for rendering backgrounds and entities it uses world units
	 */
	public static OrthographicCamera worldCamera;
	/**
	 * camera used for rendering the user interface compnents it uses pixel units
	 */
	public static OrthographicCamera uiCamera;

	/**
	 * viewport used by the worldCamera
	 */
	public static ExtendViewport extendViewport;

	/**
	 * viewport used for the user interface components
	 */
	public static ScreenViewport uiScreenViewport;

	private Sprite background;
	private Sprite sprite;

	/**
	 * the world width measured in tiles
	 */
	public static final float WORLD_WIDTH = 64;
	/**
	 * the world height measured in tiles
	 */
	public static final float WORLD_HEIGHT = 36;

	/**
	 * used to scale from pixel units to world units
	 */
	public final float scaleFactor = WORLD_HEIGHT / 1080;

	@Override
	public void create() {

		spriteBatch = new SpriteBatch();

		float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		worldCamera = new OrthographicCamera(WORLD_HEIGHT * aspectRatio, WORLD_HEIGHT);
		worldCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

		extendViewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, worldCamera);

		background = new Sprite(new Texture(Gdx.files.internal("assets/back.png")));
		sprite = new Sprite(new Texture(Gdx.files.internal("assets/pep.png")));
		sprite.setPosition(32 - sprite.getWidth() * scaleFactor / 2, 18 - sprite.getHeight() * scaleFactor / 2);
		background.setPosition(0, 0);
		background.setSize(background.getWidth() * scaleFactor, background.getHeight() * scaleFactor);
		sprite.setSize(sprite.getWidth() * scaleFactor, sprite.getHeight() * scaleFactor);
		System.out.print(aspectRatio + " " + Gdx.graphics.getHeight() + " " + worldCamera.viewportHeight + " "
				+ sprite.getHeight());

		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);
		worldCamera.update();
		extendViewport.apply();
		spriteBatch.setProjectionMatrix(worldCamera.combined);

		spriteBatch.begin();
		background.draw(spriteBatch);

		sprite.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		extendViewport.update(width, height);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.RIGHT)
			worldCamera.translate(1f, 0f);
		if (keycode == Input.Keys.LEFT)
			worldCamera.translate(-1f, 0f);
		if (keycode == Input.Keys.UP)
			worldCamera.translate(0f, 1f);
		if (keycode == Input.Keys.DOWN)
			worldCamera.translate(0f, -1f);

		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float arg0, float arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
