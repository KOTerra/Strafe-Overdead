package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;

public class EntityTypeComponent implements Component {
	public EntityType entityType;
	public EntityState entityState = EntityState.idle;

	public void reset() {
		this.entityType = null;
		this.entityState = EntityState.idle;
	}
}
