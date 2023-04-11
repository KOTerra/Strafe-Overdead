package com.strafergame.game.ecs.system.render;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;

public class RenderingSystem extends SortedIteratingSystem {

	private SpriteBatch batch; // a reference to our spritebatch
	private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of
										// each other
	private Comparator<Entity> comparator = new ZComparator();

	private ComponentMapper<SpriteComponent> spriteMapper;
	private ComponentMapper<PositionComponent> positionMapper;

	public RenderingSystem(SpriteBatch batch) {
		super(Family.all(SpriteComponent.class, PositionComponent.class).get(), new ZComparator());

		this.batch = batch;
		spriteMapper = ComponentMappers.sprite;
		positionMapper = ComponentMappers.position;

		renderQueue = new Array<Entity>();
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		renderQueue.sort(comparator);

		batch.enableBlending();
		batch.begin();

		// loop through each entity in our render queue
		for (Entity entity : renderQueue) {
			/**
			 * TextureComponent tex = textureM.get(entity); TransformComponent t =
			 * transformM.get(entity);
			 * 
			 * if (tex.region == null || t.isHidden) { continue; }
			 * 
			 * float width = tex.region.getRegionWidth(); float height =
			 * tex.region.getRegionHeight();
			 * 
			 * float originX = width / 2f; float originY = height / 2f;
			 * 
			 * batch.draw(tex.region, t.position.x - originX, t.position.y - originY,
			 * originX, originY, width, height, PixelsToMeters(t.scale.x),
			 * PixelsToMeters(t.scale.y), t.rotation);
			 **/
		}

		batch.end();
		renderQueue.clear();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}

}