package com.strafergame.assets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.states.EntityType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class AnimationProvider {

    protected static final Map<EntityType, HashMap<String, Animation<Sprite>>> TYPE_ANIMATIONS = new EnumMap<>(
            EntityType.class);


    /**
     * for ecs
     *
     * @param entity
     * @return
     */
    public static Animation<Sprite> getAnimation(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        if (posCmp == null) {
            return TYPE_ANIMATIONS.get(typeCmp.entityType).get(typeCmp.entityState.toString());
        }
        //System.out.println(typeCmp.entityType+" "+typeCmp.entityState+ " "+posCmp.direction);
        return TYPE_ANIMATIONS.get(typeCmp.entityType).get(typeCmp.entityState + "_" + posCmp.direction);
    }

    public static void prepareAnimations() {//to change
        for (EntityType e : EntityType.values()) {
            TYPE_ANIMATIONS.put(e, new HashMap<>());
        }
        AnimationFactory.prepareAnimations();
    }

    private AnimationProvider() {
    }


}
