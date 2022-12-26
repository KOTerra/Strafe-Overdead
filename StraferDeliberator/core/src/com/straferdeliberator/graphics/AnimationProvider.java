package com.straferdeliberator.graphics;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;
import com.straferdeliberator.game.entity.EntityState;

public class AnimationProvider {

	public static final float FRAME_DURATION = 0.25f;

	static final HashMap<String, Animation<TextureRegion>> PLAYER_ANIMATIONS = new HashMap<>();

	public static Animation<TextureRegion> getAnimation(Entity entity) {
		switch (entity.getEntityType()) {
		case PLAYER: {
			return PLAYER_ANIMATIONS
					.get(EntityState.asString(entity.getEntityState()) + "_" + entity.getDirectionName());
		}
		}
		return null;
	}

	private AnimationProvider() {
	}

	public static void prepareAnimations() {
		PLAYER_ANIMATIONS.put("idle_w",
				new Animation<TextureRegion>(FRAME_DURATION, Strafer.assetManager
						.get("spritesheets/player/player.atlas", TextureAtlas.class).findRegions("player_idle_w"),
						PlayMode.LOOP));
		PLAYER_ANIMATIONS.put("idle_a",
				new Animation<TextureRegion>(FRAME_DURATION, Strafer.assetManager
						.get("spritesheets/player/player.atlas", TextureAtlas.class).findRegions("player_idle_a"),
						PlayMode.LOOP));
		PLAYER_ANIMATIONS.put("idle_s",
				new Animation<TextureRegion>(FRAME_DURATION, Strafer.assetManager
						.get("spritesheets/player/player.atlas", TextureAtlas.class).findRegions("player_idle_s"),
						PlayMode.LOOP));
		PLAYER_ANIMATIONS.put("idle_d",
				new Animation<TextureRegion>(FRAME_DURATION, Strafer.assetManager
						.get("spritesheets/player/player.atlas", TextureAtlas.class).findRegions("player_idle_d"),
						PlayMode.LOOP));
	}

}
