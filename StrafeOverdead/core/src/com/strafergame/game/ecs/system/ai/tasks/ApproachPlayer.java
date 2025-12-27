package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.GameWorld;

public class ApproachPlayer extends LeafTask<Entity> {

    @Override
    public Status execute() {
        Entity e = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(e);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        if (steerCmp == null) return Status.FAILED;

        typeCmp.entityState = EntityState.walk;

        //  Set the Behavior intention(will update in MovementSystem)
        if (!(steerCmp.behavior instanceof Seek) && GameWorld.player != null) {
            SteeringComponent playerSteer = ComponentMappers.steering().get(GameWorld.player);
            if (playerSteer != null) {
                steerCmp.behavior = new Seek<>(steerCmp, playerSteer);
            }
        }


        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachPlayer();
    }
}