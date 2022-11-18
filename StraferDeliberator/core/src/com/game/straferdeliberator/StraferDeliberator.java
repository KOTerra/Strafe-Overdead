package com.game.straferdeliberator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The game class
 * @author mihai_stoica
 */
public class StraferDeliberator extends ApplicationAdapter {

	/**
	 * the asset manager
	 */
	public static AssetManager assetManager;
	/**
	 * the sprite batch
	 */
	public static SpriteBatch spriteBatch;

	/**
	 * camera used for rendering backgrounds and entities
	 * it uses world units
	 */
	private OrthographicCamera camera;
	/**
	 * camera used for rendering the ui
	 * it uses pixel units
	 */
	private OrthographicCamera uiCamera;
	
	
	public static Sprite background;
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
	public final float scaleFactor=WORLD_HEIGHT/1080;
	
	@Override
	public void create() {

		spriteBatch = new SpriteBatch();
		
		float aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
		camera = new OrthographicCamera(WORLD_HEIGHT * aspectRatio, WORLD_HEIGHT);
		camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
		
		
		background = new Sprite(new Texture(Gdx.files.internal("assets/back.png")));
		sprite = new Sprite(new Texture(Gdx.files.internal("assets/pep.png")));
		sprite.setPosition(32-sprite.getWidth()*scaleFactor/2, 18-sprite.getHeight()*scaleFactor/2);
		background.setPosition(0, 0);
		background.setSize(background.getWidth()*scaleFactor,background.getHeight()*scaleFactor);
		sprite.setSize(sprite.getWidth()*scaleFactor,sprite.getHeight()*scaleFactor);
		System.out.print(
				aspectRatio + " " + Gdx.graphics.getHeight() + " " + camera.viewportHeight + " " + sprite.getHeight());

	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 0, 0, 1);
		camera.update();

		spriteBatch.setProjectionMatrix(camera.combined);

		spriteBatch.begin();
		background.draw(spriteBatch);

		sprite.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
