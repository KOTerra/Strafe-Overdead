package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.game.ecs.states.EntityType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Registry to store and retrieve entity creators for dynamic creation.
 */
public class EntityRegistry {
    private static final Map<EntityType, EntityCreator> creators = new EnumMap<>(EntityType.class);

    /**
     * Register an entity creator for a specific type.
     */
    public static void register(EntityType type, EntityCreator creator) {
        creators.put(type, creator);
    }

    /**
     * Create an entity by its registered type name.
     */
    public static Entity create(EntityType type, Vector3 position, MapObject mapObject) {
        if (type == null) {
            return null;
        }
        EntityCreator creator = creators.get(type);
        if (creator == null) {
            return null;
        }
        return creator.create(position, mapObject);
    }

    public static void clear() {
        creators.clear();
    }
}
