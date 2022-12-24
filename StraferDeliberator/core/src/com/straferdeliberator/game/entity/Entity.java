package com.straferdeliberator.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.assets.graphics.AnimationProvider;
import com.straferdeliberator.game.world.collision.Box2DHelper;
import com.straferdeliberator.game.world.collision.Box2DWorld;

/**
 * A physics object that resides in the gameworld.
 * 
 * @author mihai_stoica
 */
public class Entity extends Actor {

	protected EntityType entityType;

	protected EntityState entityState;

	protected Animation<TextureRegion> animation;// regions taken from TextureAtlas
	// maybe add animations themselves in asset manager or make them static fields

	private TextureRegion currentFrame;

	protected Body body;
	protected Box2DWorld box2DWorld;

	protected float speed;
	protected float dirX;
	protected float dirY;

	private boolean centered = false;

	public Entity() {

	}

	@Override
	public void act(float delta) {
		updateRegion(delta);
		updateCenter();
	}

	private void updateRegion(float delta) {
		animation = AnimationProvider.getAnimation(this);
		currentFrame = animation.getKeyFrame(Strafer.getStateTime(), true);
		setSize(currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR,
				currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR);
	}

	private void updateCenter() {
		if (!centered) {
			setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
			this.box2DWorld = Strafer.gameWorld.getBox2DWorld();
			body = Box2DHelper.createBody(box2DWorld.getWorld(), getWidth(), getHeight(), 0, 0,
					new Vector3(getX(), getY(), 0), BodyType.DynamicBody);
			centered = true;

		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setScale(Strafer.SCALE_FACTOR);
		batch.draw(currentFrame, getX(), getY(), // coordonatele
				getWidth() / 2, getHeight() / 2, // pct in care e rotit
				getWidth(), getHeight(), // width/height
				1, 1, // scale
				getRotation()); // rotation
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public EntityState getEntityState() {
		return entityState;
	}

}
