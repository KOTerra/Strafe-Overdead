package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarPathfinder;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.map.MapManager;

public class ApproachPlayerAStar extends LeafTask<Entity> {

    private float timeSinceLastPathUpdate = 0;
    private static final float PATH_UPDATE_INTERVAL = 1.0f; // Update path every second
    private static final float STOP_DISTANCE = 2.0f; // Distance at which to stop to avoid pushing player

    @Override
    public Status execute() {
        Entity entity = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (steerCmp == null || GameWorld.player == null) {
            return Status.FAILED;
        }

        // Check distance to player
        Vector2 playerPos = ComponentMappers.box2d().get(GameWorld.player).body.getPosition();
        if (b2dCmp.body.getPosition().dst(playerPos) < STOP_DISTANCE) {
            return Status.FAILED; // Break chase sequence and go to idle
        }

        ComponentMappers.entityType().get(entity).entityState = EntityState.walk;

        timeSinceLastPathUpdate += 0.016f; // Rough estimate of delta time

        if (timeSinceLastPathUpdate >= PATH_UPDATE_INTERVAL) {
            updatePathfinder(entity, b2dCmp, steerCmp);
            timeSinceLastPathUpdate = 0;
        }

        return Status.SUCCEEDED;
    }

    private void updatePathfinder(Entity entity, Box2dComponent b2dCmp, SteeringComponent steerCmp) {
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        AStarPathfinder pathfinder = MapManager.getPathfinder(elevation);

        Vector2 start = b2dCmp.body.getPosition();
        Vector2 end = ComponentMappers.box2d().get(GameWorld.player).body.getPosition();

        Array<Vector2> waypoints = pathfinder.findPath(start, end);
        steerCmp.debugPath = waypoints;

        // Gather neighbors for separation
        Array<Steerable<Vector2>> neighbors = new Array<>();
        for (Entity other : EntityEngine.getInstance().getEntitiesFor(Family.all(SteeringComponent.class).get())) {
            if (other != entity) {
                neighbors.add(ComponentMappers.steering().get(other));
            }
        }
        RadiusProximity<Vector2> proximity = new RadiusProximity<>(steerCmp, neighbors, 1.2f);
        Separation<Vector2> separation = new Separation<>(steerCmp, proximity);

        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<>(steerCmp);
        prioritySteering.add(separation);

        if (waypoints != null && waypoints.size > 1) {
            LinePath<Vector2> path = new LinePath<>(waypoints, false);
            FollowPath<Vector2, LinePath.LinePathParam> followPath = new FollowPath<>(steerCmp, path, 1.0f);
            prioritySteering.add(followPath);
        } else {
            // Fallback to simple Seek
            prioritySteering.add(new Seek<>(steerCmp, ComponentMappers.steering().get(GameWorld.player)));
            steerCmp.debugPath = null;
        }

        steerCmp.behavior = prioritySteering;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachPlayerAStar();
    }
}
