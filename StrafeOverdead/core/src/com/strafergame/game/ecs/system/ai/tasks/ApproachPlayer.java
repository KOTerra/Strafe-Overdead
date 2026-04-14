package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
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

        // Gather neighbors for separation
        Array<Steerable<Vector2>> neighbors = new Array<>();
        for (Entity other : EntityEngine.getInstance().getEntitiesFor(Family.all(SteeringComponent.class).get())) {
            if (other != e) {
                neighbors.add(ComponentMappers.steering().get(other));
            }
        }
        RadiusProximity<Vector2> proximity = new RadiusProximity<>(steerCmp, neighbors, 1.2f);
        Separation<Vector2> separation = new Separation<>(steerCmp, proximity);

        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<>(steerCmp);
        prioritySteering.add(separation);
        prioritySteering.add(new Seek<>(steerCmp, ComponentMappers.steering().get(GameWorld.player)));

        steerCmp.behavior = prioritySteering;
        steerCmp.debugPath = null;

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachPlayer();
    }


}