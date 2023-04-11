package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.entities.EntityDirection;

public class PositionComponent implements Component, Poolable {
	public float x = 0f;
	public float y = 0f;
	public EntityDirection direction = EntityDirection.s;

	@Override
	public void reset() {
		this.x = 0f;
		this.y = 0f;
		this.direction = EntityDirection.s;
	}
}
