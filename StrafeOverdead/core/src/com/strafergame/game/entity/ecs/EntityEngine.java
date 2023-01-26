package com.strafergame.game.entity.ecs;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Disposable;

public class EntityEngine extends PooledEngine implements Disposable {

	public EntityEngine() {
		super();

	}

	@Override
	public void dispose() {
	}

}
