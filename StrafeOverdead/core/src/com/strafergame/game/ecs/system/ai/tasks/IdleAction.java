package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityState;

public class IdleAction extends LeafTask<Entity> {

    @Override
    public Status execute() {
        Entity e = getObject();
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
        SteeringComponent steerCmp = ComponentMappers.steering().get(e);

        typeCmp.entityState = EntityState.idle;

        // disable Steering
        if (steerCmp != null) {
            steerCmp.behavior = null;
            steerCmp.steeringOutput.setZero();
        }
        // stop physics immediately
        if (b2dCmp != null && b2dCmp.body != null) {
            b2dCmp.body.setLinearVelocity(0, 0);
            b2dCmp.body.setAngularVelocity(0);
        }

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new IdleAction();
    }
}