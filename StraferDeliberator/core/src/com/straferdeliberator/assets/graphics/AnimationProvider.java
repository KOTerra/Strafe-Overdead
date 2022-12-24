package com.straferdeliberator.assets.graphics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;

public class AnimationProvider {

	public static final float FRAME_DURATION = 0.35f;

	static final Animation<TextureRegion> PLAYER_IDLE_S = new Animation<TextureRegion>(FRAME_DURATION,
			Strafer.assetManager.get("spritesheets/player/player.atlas", TextureAtlas.class)
					.findRegions("player_idle_s"),
			PlayMode.LOOP);

	public static Animation<TextureRegion> getAnimation(Entity entity) {
		Animation<TextureRegion> animation = PLAYER_IDLE_S;
		System.out.println(entity.getEntityType().toString().toLowerCase());
		return animation;
	}

	private AnimationProvider() {
	}
}
