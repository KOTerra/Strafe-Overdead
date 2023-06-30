package com.strafergame.game.ecs.system.render;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;

public class RenderingSystem extends SortedIteratingSystem {

	private SpriteBatch batch; // a reference to our spritebatch
	String vertexShader;
	String fragmentShader;
	ShaderProgram shaderProgram;

	private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of
										// each other
	private Comparator<Entity> comparator = new ZComparator();

	private ComponentMapper<SpriteComponent> spriteMapper;
	private ComponentMapper<PositionComponent> positionMapper;

	public RenderingSystem(SpriteBatch batch) {
		super(Family.all(SpriteComponent.class, PositionComponent.class).get(), new ZComparator());

		this.batch = batch;
		vertexShader = Gdx.files.internal("shaders/default.vert").readString();
		fragmentShader = Gdx.files.internal("shaders/default.frag").readString();
		shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
	

		spriteMapper = ComponentMappers.sprite();
		positionMapper = ComponentMappers.position();

		renderQueue = new Array<Entity>();
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		renderQueue.sort(comparator);

		batch.enableBlending();
		batch.begin();
		batch.setShader(shaderProgram);

		shaderProgram.setUniformf("u_resolution", new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));


		// loop through each entity in our render queue
		for (Entity entity : renderQueue) {
			SpriteComponent spriteCmp = spriteMapper.get(entity);
			PositionComponent posCmp = positionMapper.get(entity);
			if (spriteCmp.sprite == null || posCmp.isHidden) {
				continue;
			}
			// System.err.println(spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR);

			batch.draw(spriteCmp.sprite, posCmp.renderX - spriteCmp.width / 2, posCmp.renderY, // -
																								// getHeight()
					// / 2, //
					// coordonatele
					spriteCmp.width / 2, 0, // pct in care e rotit,centru
					spriteCmp.width, spriteCmp.height, // width/height
					1, 1, // scale
					spriteCmp.sprite.getRotation()); // rotation

		}

		batch.end();
		renderQueue.clear();
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}

}