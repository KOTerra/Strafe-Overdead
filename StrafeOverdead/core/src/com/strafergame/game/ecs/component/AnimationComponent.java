package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AnimationComponent implements Component, Poolable {
	public Animation<Sprite> animation;
	public Sprite currentFrame;
	public float timer = 0f;
	public float width = 0f;
	public float height = 0f;

	@Override
	public void reset() {
		this.animation = null;
		currentFrame = null;
		this.timer = 0f;
		this.width = 0f;
		this.width = 0f;
	}

}
