package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.GameWorld;

public class ApproachPlayer extends LeafTask<Entity> {

    private static final float STOP_DISTANCE = 2.0f; // Distance at which to stop to avoid pushing player

    @Override
    public Status execute() {
        Entity e = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(e);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);

        if (steerCmp == null || GameWorld.player == null) return Status.FAILED;

        // Check distance to player
        Vector2 playerPos = ComponentMappers.box2d().get(GameWorld.player).body.getPosition();
        if (b2dCmp.body.getPosition().dst(playerPos) < STOP_DISTANCE) {
            return Status.FAILED; // Break chase sequence and go to idle
        }

        typeCmp.entityState = EntityState.walk;

        //  Set the Behavior intention(will update in MovementSystem)
        if (!(steerCmp.behavior instanceof Seek)) {
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