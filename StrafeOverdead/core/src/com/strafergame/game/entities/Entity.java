package com.strafergame.game.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
public class Entity {

	/**
	 * type of the entity
	 */
	protected EntityType entityType;

	/**
	 * state of the entity
	 */
	protected EntityState entityState = EntityState.idle;

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

	private float prevX = 0f;
	private float prevY = 0f;
	/**
	 * the body used for collision handling
	 */
	protected Body body;

	protected GameWorld gameWorld;

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
	 * the current direction
	 */
	protected EntityDirection direction = EntityDirection.s;

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

	public void act() {
		updateAnimation();
		initPhysics();
		move();
	}

	/**
	 * creates the body and sets the position to the center of the acto. Called only
	 * in the first frame of this entity
	 */
	private void initPhysics() {
		if (!initiatedPhysics) {
			this.box2DWorld = gameWorld.getBox2DWorld();
			prevX = -currentFrame.getWidth() * Strafer.SCALE_FACTOR / 2;
			prevY = -currentFrame.getHeight() * Strafer.SCALE_FACTOR / 2;

			body = Box2DHelper.createBody(box2DWorld.getWorld(), currentFrame.getWidth() * Strafer.SCALE_FACTOR,
					currentFrame.getWidth() * Strafer.SCALE_FACTOR, 0, 0, new Vector3(prevX, prevY, 0),
					BodyType.DynamicBody);

			initiatedPhysics = true;

		}
	}

	/**
	 * changes the postion of the entity
	 */
	protected void move() {
		body.setLinearVelocity(dirX * speed, dirY * speed);
	}

	public void savePosition() {
		prevX = body.getPosition().x;
		prevY = body.getPosition().y;
	}

	/**
	 * changes the current frame and the size
	 * 
	 */
	private void updateAnimation() {
		animation = AnimationProvider.getAnimation(this);
		currentFrame = animation.getKeyFrame(Strafer.getStateTime(), true);

		// setSize(currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR,
		// currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR);
	}

	public void draw(Batch batch) {
		renderX = MathUtils.lerp(prevX, body.getPosition().x, gameWorld.getInterPolationAlpha());
		renderY = MathUtils.lerp(prevY, body.getPosition().y, gameWorld.getInterPolationAlpha());
		batch.draw(currentFrame, renderX - currentFrame.getWidth() * Strafer.SCALE_FACTOR / 2, renderY, // - getHeight()
																										// / 2, //
																										// coordonatele
				currentFrame.getWidth() * Strafer.SCALE_FACTOR / 2, 0, // pct in care e rotit,centru
				currentFrame.getWidth() * Strafer.SCALE_FACTOR, currentFrame.getHeight() * Strafer.SCALE_FACTOR, // width/height
				1, 1, // scale
				currentFrame.getRotation()); // rotation
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
	public EntityDirection getDirection() {
		return direction;
	}

	public float getDirX() {
		return dirX;
	}

	public float getDirY() {
		return dirY;
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	public void setGameWorld(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}

}
