package com.strafergame.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.states.EntityType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;

public class AnimationProvider {

    public static final EnumMap<EntityType, HashMap<String, Animation<Sprite>>> TYPE_ANIMATIONS = new EnumMap<>(
            EntityType.class);


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

    public static void prepareAnimations() {//for
        for (EntityType e : EntityType.values()) {
            TYPE_ANIMATIONS.put(e, new HashMap<String, Animation<Sprite>>());
        }
        AnimationFactory.prepareAnimations();
    }

    private AnimationProvider() {
    }


}
