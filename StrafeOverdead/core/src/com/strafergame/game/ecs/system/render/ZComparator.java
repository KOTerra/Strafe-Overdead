package com.strafergame.game.ecs.system.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.PositionComponent;

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
        float ae = posCmp.get(a).elevation;
        float be = posCmp.get(b).elevation;

        if (Float.compare(be, ae) == 0) {
            return Float.compare(by, ay);
        }
		return Float.compare(be,ae);
    }

}