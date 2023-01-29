package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AnimationComponent;

public class AnimationSystem extends IteratingSystem {
	public AnimationSystem() {
		super(Family.all(AnimationComponent.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final AnimationComponent aniCmp = ComponentMappers.animation.get(entity);

		aniCmp.timer += deltaTime;
		TextureRegion currentFrame = aniCmp.animation.getKeyFrame(aniCmp.timer);
		aniCmp.width = currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR;
		aniCmp.height = currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR;

	}
}
