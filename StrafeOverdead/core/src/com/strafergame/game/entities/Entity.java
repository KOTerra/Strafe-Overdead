package com.strafergame.game.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.strafergame.Strafer;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DHelper;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.graphics.AnimationProvider;

/**
 * A physics object that resides in the gameworld.
 * 
 * @author mihai_stoica
 */
public class Entity extends Actor {

	/**
	 * type of the entity
	 */
	protected EntityType entityType;

	/**
	 * state of the entity
	 */
	protected EntityState entityState = EntityState.IDLE;

	/**
	 * the used animation
	 */
	protected Animation<Sprite> animation;

	/**
	 * the frame currently displayed
	 */
	private Sprite currentFrame;

	private float renderX = 0f;
	private float renderY = 0f;

	/**
	 * the body used for collision handling
	 */
	protected Body body;

	/**
	 * the collision handler
	 */
	protected Box2DWorld box2DWorld;

	/**
	 * the base speed of the entity
	 */
	protected float speed;

	/**
	 * the direction on the X axis. can be -1, 0 , 1
	 */
	protected float dirX;

	/**
	 * the direction on the Y axis. can be -1, 0 , 1
	 */
	protected float dirY;

	/**
	 * the overall direction w,a,s or d
	 */
	protected char direction = 's';

	/**
	 * whether the physics were initiated for this entity
	 */
	private boolean initiatedPhysics = false;

	/**
	 * Constructor
	 * 
	 * @param type - type of the entity
	 */
	public Entity(EntityType type) {
		this.entityType = type;
		this.speed = entityType.speed;
	}

	@Override
	public void act(float delta) {
		updateAnimation();
		initPhysics();
		move(delta);
	}

	/**
	 * creates the body and sets the position to the center of the acto. Called only
	 * in the first frame of this entity
	 */
	private void initPhysics() {
		if (!initiatedPhysics) {
			renderX = -getWidth() / 2;
			renderX = -getHeight() / 2;
			this.box2DWorld = ((GameWorld) getStage()).getBox2DWorld();
			body = Box2DHelper.createBody(box2DWorld.getWorld(), getWidth(), getWidth(), 0, 0,
					new Vector3(renderX, renderY, 0), BodyType.DynamicBody);
			initiatedPhysics = true;

		}
	}

	/**
	 * changes the postion of the entity
	 */
	protected void move(float delta) {
		body.setLinearVelocity(dirX * speed, dirY * speed);
		renderX = body.getPosition().x * delta + renderX * (1 - delta);
		renderY = body.getPosition().y * delta + renderY * (1 - delta);
	}

	/**
	 * changes the current frame and the size
	 * 
	 */
	private void updateAnimation() {
		animation = AnimationProvider.getAnimation(this);
		currentFrame = animation.getKeyFrame(Strafer.getStateTime(), true);
		setSize(currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR,
				currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setScale(Strafer.SCALE_FACTOR);
		batch.draw(currentFrame, renderX - getWidth() / 2, renderY, // - getHeight() / 2, // coordonatele
				getWidth() / 2, 0, // pct in care e rotit,centru
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

	public Body getBody() {
		return body;
	}

	public float getRenderX() {
		return renderX;
	}

	public float getRenderY() {
		return renderY;
	}

	/**
	 * based on the entity s orientation returns one of the chars w a s or d
	 * 
	 * @return the direction name
	 */
	public char getDirectionName() {
		return direction;
	}

	public float getDirX() {
		return dirX;
	}

	public float getDirY() {
		return dirY;
	}

}
