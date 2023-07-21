package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.ecs.states.EntityType;

public class CameraComponent implements Component, Poolable {

	public EntityType type;

	@Override
	public void reset() {

	}

}
