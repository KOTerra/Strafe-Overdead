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
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.EntityType;

public class AnimationProvider {

	public static final float FRAME_DURATION = 0.25f;

	static final EnumMap<EntityType, HashMap<String, Animation<Sprite>>> TYPE_ANIMATIONS = new EnumMap<>(
			EntityType.class);

	static final HashMap<String, Animation<Sprite>> PLAYER_ANIMATIONS = new HashMap<>();

	public static Animation<Sprite> getAnimation(Entity entity) {
		return TYPE_ANIMATIONS.get(entity.getEntityType()).get(entity.getEntityState() + "_" + entity.getDirection());

	}

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
		}

		return TYPE_ANIMATIONS.get(typeCmp.entityType).get(typeCmp.entityState + "_" + posCmp.direction);
	}

	private AnimationProvider() {
	}

	public static void prepareAnimations() {
		PLAYER_ANIMATIONS.put("idle_w", makeSprites(0.25f, EntityType.player, "idle_w"));
		PLAYER_ANIMATIONS.put("idle_a", makeSprites(0.25f, EntityType.player, "idle_a"));
		PLAYER_ANIMATIONS.put("idle_s", makeSprites(0.25f, EntityType.player, "idle_s"));
		PLAYER_ANIMATIONS.put("idle_d", makeSprites(0.25f, EntityType.player, "idle_d"));
		TYPE_ANIMATIONS.put(EntityType.player, PLAYER_ANIMATIONS);

	}

	private static Animation<Sprite> makeSprites(float duration, EntityType entityType, String animation) {
		Array<Sprite> array = new Array<>();

		for (AtlasRegion a : Strafer.assetManager
				.get("spritesheets/" + entityType + "/" + entityType + ".atlas", TextureAtlas.class)
				.findRegions(entityType + "_" + animation)) {
			a.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Nearest);
			// a.getTexture().setAnisotropicFilter(10);
			Sprite s = new Sprite(a);

			s.setScale(Strafer.SCALE_FACTOR);
			array.add(s);

		}
		return new Animation<Sprite>(duration, array, PlayMode.LOOP);
	}

}
