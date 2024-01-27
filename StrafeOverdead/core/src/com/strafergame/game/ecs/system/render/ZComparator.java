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
        return Float.compare(by, ay);
    }

}