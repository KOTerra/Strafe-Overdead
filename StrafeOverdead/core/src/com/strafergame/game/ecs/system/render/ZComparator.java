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
        float ay = posCmp.get(a).renderPos.y;
        float by = posCmp.get(b).renderPos.y;
        int ae = posCmp.get(a).elevation;
        int be = posCmp.get(b).elevation;

        if (posCmp.get(b).isMapLayer && posCmp.get(a).isMapLayer) {
            return 0;
        }

        if (be == ae) {
            if (posCmp.get(b).isMapLayer) {
                return 1;
            }
            if (posCmp.get(a).isMapLayer) {
                return -1;
            }
            return Float.compare(by, ay);
        }

        return  ae-be;
    }

}