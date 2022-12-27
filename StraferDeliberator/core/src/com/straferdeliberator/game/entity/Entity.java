package com.straferdeliberator.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.world.collision.Box2DHelper;
import com.straferdeliberator.game.world.collision.Box2DWorld;
import com.straferdeliberator.graphics.AnimationProvider;

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
	protected Animation<TextureRegion> animation;

	/**
	 * the frame currently displayed
	 */
	private TextureRegion currentFrame;

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
			setPosition(getX() - getWidth() / 2, getY() - getHeight() / 2);
			this.box2DWorld = Strafer.gameWorld.getBox2DWorld();
			body = Box2DHelper.createBody(box2DWorld.getWorld(), getWidth(), getHeight(), 0, 0,
					new Vector3(getX(), getY(), 0), BodyType.DynamicBody);
			initiatedPhysics = true;

		}
	}

	/**
	 * changes the postion of the entity
	 */
	protected void move(float delta) {
		body.setLinearVelocity(dirX * speed, dirY * speed);
		this.setPosition(body.getPosition().x, body.getPosition().y);
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
		batch.draw(currentFrame, getX() - getWidth() / 2, getY() - getHeight() / 2, // coordonatele
				getWidth() / 2, getHeight() / 2, // pct in care e rotit,centru
				getWidth(), getHeight(), // width/height
				1, 1, // scale
				getRotation()); // rotation
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		if (body != null) {
			body.setTransform(x, y, getRotation());
		}
	}

	@Override
	public void setRotation(float angle) {
		super.setRotation(angle);
		if (body != null) {
			body.setTransform(getX(), getY(), getRotation());
		}
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
