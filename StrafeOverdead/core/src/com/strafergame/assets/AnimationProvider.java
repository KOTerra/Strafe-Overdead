package com.strafergame.assets;

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

    public static final EnumMap<EntityType, HashMap<String, Animation<Sprite>>> TYPE_ANIMATIONS = new EnumMap<>(
            EntityType.class);

    public static final HashMap<String, Animation<Sprite>> PLAYER_ANIMATIONS = new HashMap<>();

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

    public static void prepareAnimations() {
        AnimationFactory.prepareAnimations();
        TYPE_ANIMATIONS.put(EntityType.player, PLAYER_ANIMATIONS);
    }

    private AnimationProvider() {
    }


}
