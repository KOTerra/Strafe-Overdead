package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.graphics.AnimationProvider;

public class AnimationSystem extends IteratingSystem {
	public AnimationSystem() {
		super(Family.all(AnimationComponent.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final AnimationComponent aniCmp = ComponentMappers.animation.get(entity);

		aniCmp.timer += deltaTime;

		// aniCmp.animation = AnimationProvider.getAnimation(entity);
		aniCmp.currentFrame = aniCmp.animation.getKeyFrame(aniCmp.timer, true);
		aniCmp.width = aniCmp.currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR;
		aniCmp.height = aniCmp.currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR;

	}
}
