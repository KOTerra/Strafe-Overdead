package com.strafergame.game.ecs.system.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {

	private SpriteBatch batch; // a reference to our spritebatch
	private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of
										// each other
	private Comparator<Entity> comparator; // a comparator to sort images based on the z position of the
											// transfromComponent

	public RenderingSystem(SpriteBatch batch) {
		// gets all entities with a TransofmComponent and TextureComponent
		super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());

		// creates out componentMappers
		textureM = ComponentMapper.getFor(TextureComponent.class);
		transformM = ComponentMapper.getFor(TransformComponent.class);

		// create the array for sorting entities
		renderQueue = new Array<Entity>();

		this.batch = batch; // set our batch to the one supplied in constructor

		// set up the camera to match our screen size
		cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		// sort the renderQueue based on z index
		renderQueue.sort(comparator);

		// update camera and sprite batch
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.enableBlending();
		batch.begin();

		// loop through each entity in our render queue
		for (Entity entity : renderQueue) {
			TextureComponent tex = textureM.get(entity);
			TransformComponent t = transformM.get(entity);

			if (tex.region == null || t.isHidden) {
				continue;
			}

			float width = tex.region.getRegionWidth();
			float height = tex.region.getRegionHeight();

			float originX = width / 2f;
			float originY = height / 2f;

			batch.draw(tex.region, t.position.x - originX, t.position.y - originY, originX, originY, width, height,
					PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y), t.rotation);
		}

		batch.end();
		renderQueue.clear();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}

}