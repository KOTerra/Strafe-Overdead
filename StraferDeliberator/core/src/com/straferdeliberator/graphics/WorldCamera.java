package com.straferdeliberator.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;

/**
 * an orthographic camera which follows an assigned entity, mainly the player
 * 
 * @author mihai_stoica
 *
 */
public class WorldCamera extends OrthographicCamera {

	Entity focusEntity;

	boolean focused = false;

	Vector3 cameraSnapPosition = new Vector3();

	public WorldCamera(float width, float height) {
		super(width, height);
	}

	public void setFocusOn(Entity entity) {
		this.focusEntity = entity;
		focused = true;
	}

	@Override
	public void update() {
		super.update();

		if (focused) {
			cameraSnapPosition.set(focusEntity.getX(), focusEntity.getY(), 0);

			Strafer.worldCamera.position.interpolate(cameraSnapPosition, 0.05f, Interpolation.linear);

		}
	}
}
