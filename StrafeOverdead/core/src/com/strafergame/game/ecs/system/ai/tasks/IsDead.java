package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.states.EntityState;

public class IsDead extends LeafTask<Entity> {
    @Override
    public Status execute() {
        Entity entity = getObject();
        EntityState state = ComponentMappers.entityType().get(entity).entityState;

        return state == EntityState.death ? Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}