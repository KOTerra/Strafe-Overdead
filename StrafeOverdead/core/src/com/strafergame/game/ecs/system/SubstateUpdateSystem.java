package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.states.EntityState;

public class SubstateUpdateSystem extends IteratingSystem {
    public SubstateUpdateSystem() {
        super(Family.all(EntityTypeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (!typeCmp.entityState.isWithSubstates()) {
            typeCmp.entitySubState = EntityState.NoneSubstate.none;
        }
    }
}
