package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector3;

/**
 * Functional interface for creating entities.
 */
@FunctionalInterface
public interface EntityCreator {
    Entity create(Vector3 position, MapObject mapObject);
}
