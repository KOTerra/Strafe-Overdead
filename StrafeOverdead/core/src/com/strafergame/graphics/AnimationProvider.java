package com.strafergame.graphics;

import java.util.EnumMap;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;

public class AnimationProvider {

	public static final float FRAME_DURATION = 0.25f;

	static final EnumMap<EntityType, HashMap<String, Animation<Sprite>>> TYPE_ANIMATIONS = new EnumMap<>(
			EntityType.class);

	static final HashMap<String, Animation<Sprite>> PLAYER_ANIMATIONS = new HashMap<>();

	/**
	 * for ecs
	 * 
	 * @param entity
	 * @return
	 */
	public static Animation<Sprite> getAnimation(com.badlogic.ashley.core.Entity entity) {
		EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
		PositionComponent posCmp = ComponentMappers.position().get(entity);
		if (posCmp == null) {
			return TYPE_ANIMATIONS.get(typeCmp.entityType).get(typeCmp.entityState.toString());
			// return
			// TYPE_ANIMATIONS.get(typeCmp.entityType).get(EntityState.idle.toString());
		}

		return TYPE_ANIMATIONS.get(typeCmp.entityType).get(typeCmp.entityState + "_" + posCmp.direction);
		// return TYPE_ANIMATIONS.get(typeCmp.entityType).get(EntityState.idle + "_" +
		// posCmp.direction);

	}

	private AnimationProvider() {
	}

	public static void prepareAnimations() {
		PLAYER_ANIMATIONS.put("idle_w", makeSprites(0.25f, EntityType.player, "idle_w", true));
		PLAYER_ANIMATIONS.put("idle_a", makeSprites(0.25f, EntityType.player, "idle_a", true));
		PLAYER_ANIMATIONS.put("idle_s", makeSprites(0.25f, EntityType.player, "idle_s", true));
		PLAYER_ANIMATIONS.put("idle_d", makeSprites(0.25f, EntityType.player, "idle_d", true));

		PLAYER_ANIMATIONS.put("walk_w", makeSprites(0.25f, EntityType.player, "walk_w", true));
		PLAYER_ANIMATIONS.put("walk_a", makeSprites(0.25f, EntityType.player, "walk_a", true));
		PLAYER_ANIMATIONS.put("walk_s", makeSprites(0.25f, EntityType.player, "walk_s", true));
		PLAYER_ANIMATIONS.put("walk_d", makeSprites(0.25f, EntityType.player, "walk_d", true));

		PLAYER_ANIMATIONS.put("hit_w", makeSprites(0.25f, EntityType.player, "hit_w", false));
		PLAYER_ANIMATIONS.put("hit_a", makeSprites(0.25f, EntityType.player, "hit_a", false));
		PLAYER_ANIMATIONS.put("hit_s", makeSprites(0.25f, EntityType.player, "hit_s", false));
		PLAYER_ANIMATIONS.put("hit_d", makeSprites(0.25f, EntityType.player, "hit_d", false));

		PLAYER_ANIMATIONS.put("dash_w", makeSprites(0.25f, EntityType.player, "dash_w", false));
		PLAYER_ANIMATIONS.put("dash_a", makeSprites(0.25f, EntityType.player, "dash_a", false));
		PLAYER_ANIMATIONS.put("dash_s", makeSprites(0.25f, EntityType.player, "dash_s", false));
		PLAYER_ANIMATIONS.put("dash_d", makeSprites(0.25f, EntityType.player, "dash_d", false));

		PLAYER_ANIMATIONS.put("death_w", makeSprites(0.25f, EntityType.player, "death_w", false));
		PLAYER_ANIMATIONS.put("death_a", makeSprites(0.25f, EntityType.player, "death_a", false));
		PLAYER_ANIMATIONS.put("death_s", makeSprites(0.25f, EntityType.player, "death_s", false));
		PLAYER_ANIMATIONS.put("death_d", makeSprites(0.25f, EntityType.player, "death_d", false));

		TYPE_ANIMATIONS.put(EntityType.player, PLAYER_ANIMATIONS);

	}

	private static Animation<Sprite> makeSprites(float duration, EntityType entityType, String animation,
			boolean loop) {
		Array<Sprite> array = new Array<>();

		for (AtlasRegion a : Strafer.assetManager
				.get("spritesheets/" + entityType + "/" + entityType + ".atlas", TextureAtlas.class)
				.findRegions(entityType + "_" + animation)) {
			a.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			Sprite s = new Sprite(a);

			s.setScale(Strafer.SCALE_FACTOR);
			array.add(s);

		}
		return new Animation<Sprite>(duration, array, getPlayMode(loop));
	}

	private static PlayMode getPlayMode(boolean loop) {
		return loop ? PlayMode.LOOP : PlayMode.NORMAL;
	}
}
