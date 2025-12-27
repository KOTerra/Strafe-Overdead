package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.world.GameWorld;

public class IsElevationMatching extends LeafTask<Entity> {

    @Override
    public Status execute() {
        Entity e = getObject();
        Entity target = GameWorld.player;

        if (target == null) return Status.FAILED;

        ElevationComponent enemyElv = ComponentMappers.elevation().get(e);
        ElevationComponent targetElv = ComponentMappers.elevation().get(target);

        if (enemyElv != null && targetElv != null && enemyElv.elevation == targetElv.elevation) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}