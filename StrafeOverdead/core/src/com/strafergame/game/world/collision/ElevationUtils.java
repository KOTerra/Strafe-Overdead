package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;

public class ElevationUtils {

    // --- LIGHTING BITS (Reserved Bits 8-15) ---
    public static final short LIGHT_BIT_OFFSET = 8;
    public static final short ALL_LIGHT_BITS = (short) 0xFF00;

    public static short getWallCategory(int elevation) {
        int bitShift = (elevation % 8) + LIGHT_BIT_OFFSET;
        return (short) (1 << bitShift);
    }

    /**
     * Updates fixture filters to cast shadows on a specific elevation
     * without losing existing gameplay categories (Player, Footprint, etc).
     */
    public static void setShadowFilter(Body body, int elevation) {
        if (body == null) return;
        short shadowBit = getWallCategory(elevation);
        for (Fixture fixture : body.getFixtureList()) {
            // Only solid objects (non-sensors) cast shadows
            if (!fixture.isSensor()) {
                Filter filter = fixture.getFilterData();
                filter.categoryBits &= ~ALL_LIGHT_BITS; // Clear old elevation bits
                filter.categoryBits |= shadowBit;       // Add current elevation bit
                fixture.setFilterData(filter);
            }
        }
    }

    /**
     * Sets the logical and render elevation of an entity and syncs its shadow filter.
     */
    public static void changeElevation(Entity entity, int newElevation) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (elvCmp != null) {
            elvCmp.elevation = newElevation;
        }
        if (posCmp != null) {
            posCmp.elevation = newElevation;
        }
        if (b2dCmp != null) {
            setShadowFilter(b2dCmp.body, newElevation);
        }
    }

    /**
     * Updates only the render elevation (PositionComponent) and shadow filter.
     * Used for slopes where logical elevation hasn't changed yet.
     */
    public static void changeRenderElevation(Entity entity, int newElevation) {
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (posCmp != null) {
            posCmp.elevation = newElevation;
        }
        if (b2dCmp != null) {
            setShadowFilter(b2dCmp.body, newElevation);
        }
    }
}
