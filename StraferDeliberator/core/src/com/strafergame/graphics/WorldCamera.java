package com.strafergame.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.Strafer;
import com.strafergame.game.entity.Entity;

/**
 * an orthographic camera which follows an assigned entity, mainly the player
 * 
 * @author mihai_stoica
 *
 */
public class WorldCamera extends OrthographicCamera {

	/**
	 * the entity which the camera follows
	 */
	private Entity focusEntity;

	/**
	 * whether the camera follows an entity or not
	 */
	private boolean focused = false;

	/**
	 * the position to which the camera aims to snap
	 */
	private final Vector3 cameraSnapPosition = new Vector3();

	/**
	 * the alpha used in the interpolation process
	 */
	private float alpha = 0.05f;

	/*
	 * type of interpolation
	 */
	private Interpolation interpolation = Interpolation.linear;

	/**
	 * constructor
	 * 
	 * @param width
	 * @param height
	 */
	public WorldCamera(float width, float height) {
		super(width, height);
		super.zoom = 1.1f;
	}

	/**
	 * sets the focus to entity
	 * 
	 * @param entity
	 */
	public void setFocusOn(Entity entity) {
		this.focusEntity = entity;
		focused = true;
	}

	/**
	 * sets the focus to entity with given transition parameters
	 * 
	 * @param entity
	 * @param alpha
	 * @param interpolation
	 */
	public void setFocusOn(Entity entity, float alpha, Interpolation interpolation) {
		this.focusEntity = entity;
		this.alpha = alpha;
		this.interpolation = interpolation;
		focused = true;
	}

	/**
	 * stops focusing on an entity
	 */
	public void removeFocus() {
		focused = false;
	}

	/**
	 * follows the focused entity if there is one
	 */
	@Override
	public void update() {
		super.update();

		if (focused) {
			cameraSnapPosition.set(focusEntity.getX(), focusEntity.getY(), 0);
			Strafer.worldCamera.position.interpolate(cameraSnapPosition, alpha, interpolation);
		}
	}

	public Entity getFocusEntity() {
		return focusEntity;
	}
}
