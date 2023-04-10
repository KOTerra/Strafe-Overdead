package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.strafergame.game.entities.EntityType;

public class EntityTypeComponent implements Component {
	public EntityType entityType;

	public void reset() {
		this.entityType = null;
	}
}
