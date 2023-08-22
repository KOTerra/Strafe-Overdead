package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AnimationComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.assets.AnimationProvider;

public class AnimationSystem extends IteratingSystem {
	public AnimationSystem() {
		super(Family.all(AnimationComponent.class, SpriteComponent.class).get());
	}

	@Override
	protected void processEntity(final Entity entity, final float deltaTime) {
		final AnimationComponent aniCmp = ComponentMappers.animation().get(entity);
		final SpriteComponent spriteCmp = ComponentMappers.sprite().get(entity);

		aniCmp.timer += deltaTime;

		aniCmp.animation = AnimationProvider.getAnimation(entity);
		spriteCmp.sprite = aniCmp.animation.getKeyFrame(aniCmp.timer, true);
		spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;
		spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;

	}
}
