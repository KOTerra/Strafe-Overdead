package com.strafergame.graphics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.PositionComponent;

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
	private float alpha = 0.03f;

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
		super.zoom = 1f;
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
			PositionComponent posCmp = ComponentMappers.position().get(focusEntity);
			float x = Math.round(posCmp.renderX / Strafer.SCALE_FACTOR) * Strafer.SCALE_FACTOR;
			float y = Math.round(posCmp.renderY / Strafer.SCALE_FACTOR) * Strafer.SCALE_FACTOR;

			cameraSnapPosition.set(x, y, 0);

			Strafer.worldCamera.position.interpolate(cameraSnapPosition, alpha, interpolation);
		}
	}

	public Entity getFocusEntity() {
		return focusEntity;
	}
}
