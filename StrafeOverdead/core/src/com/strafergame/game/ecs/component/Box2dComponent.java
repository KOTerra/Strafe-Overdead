package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

import box2dLight.Light;

public class Box2dComponent implements Component, Poolable {
	public boolean initiatedPhysics = false;
	public Body body;
	public Light light;
	public float lightDistance;
	public float lightFluctuationDistance;
	public float lightFluctuationTime;
	public float lightFluctuationSpeed;
	public float width;
	public float height;

	@Override
	public void reset() {
		initiatedPhysics = false;
		lightFluctuationDistance = 0;
		lightFluctuationTime = 0;
		lightDistance = 0;
		if (light != null) {
			light.remove(true);
			light = null;
		}
		if (body != null) {
			body.getWorld().destroyBody(body);
			body = null;
		}
		width = height = 0;
	}
}
