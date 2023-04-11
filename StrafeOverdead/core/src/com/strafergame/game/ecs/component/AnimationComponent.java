package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AnimationComponent implements Component, Poolable {
	public Animation<Sprite> animation;
	public float timer = 0f;

	@Override
	public void reset() {
		this.animation = null;
		this.timer = 0f;

	}

}
