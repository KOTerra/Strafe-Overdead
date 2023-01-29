package com.strafergame.graphics;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.EntityState;

public class AnimationProvider {

	public static final float FRAME_DURATION = 0.25f;

	static final HashMap<String, Animation<Sprite>> PLAYER_ANIMATIONS = new HashMap<>();

	public static Animation<Sprite> getAnimation(Entity entity) {
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
		PLAYER_ANIMATIONS.put("idle_w", makeSprites(0.25f, "player", "idle_w"));
		PLAYER_ANIMATIONS.put("idle_a", makeSprites(0.25f, "player", "idle_a"));
		PLAYER_ANIMATIONS.put("idle_s", makeSprites(0.25f, "player", "idle_s"));
		PLAYER_ANIMATIONS.put("idle_d", makeSprites(0.25f, "player", "idle_d"));

	}

	private static Animation<Sprite> makeSprites(float duration, String entity, String animation) {
		Array<Sprite> array = new Array<>();

		for (AtlasRegion a : Strafer.assetManager
				.get("spritesheets/" + entity + "/" + entity + ".atlas", TextureAtlas.class)
				.findRegions(entity + "_" + animation)) {
			Sprite s = new Sprite(a);
			s.setScale(Strafer.SCALE_FACTOR);
			array.add(s);

		}
		return new Animation<Sprite>(duration, array, PlayMode.LOOP);
	}

}
