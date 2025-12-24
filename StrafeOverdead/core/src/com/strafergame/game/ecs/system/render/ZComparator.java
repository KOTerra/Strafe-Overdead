package com.strafergame.game.ecs.system.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.PositionComponent;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<PositionComponent> posCmp;

    public ZComparator() {
        posCmp = ComponentMappers.position();
    }

    @Override
    public int compare(Entity a, Entity b) {
        PositionComponent posA = posCmp.get(a);
        PositionComponent posB = posCmp.get(b);

        float ay = posA.renderPos.y;
        float by = posB.renderPos.y;
        int ae = posA.elevation;
        int be = posB.elevation;

        // elevation Check Priority
        if (ae != be) {
            return Integer.compare(ae, be);
        }

        // Map Layer Check (On same elevation, Map is always drawn before Entity)
        boolean aIsMap = posA.isMapLayer;
        boolean bIsMap = posB.isMapLayer;

        if (aIsMap && bIsMap) {
            return 0; // Both are map layers on the same elevation
        }
        if (aIsMap) {
            return -1; // a is Map, b is Entity -> a first
        }
        if (bIsMap) {
            return 1;  // b is Map, a is Entity -> b first
        }

        // Standard y sort for entities on same elevation
        return Float.compare(by, ay);
    }
}