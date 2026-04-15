package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
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

public class ApproachTargetAStar extends LeafTask<Entity> {

    private float timeSinceLastPathUpdate = 0;
    private static final float PATH_UPDATE_INTERVAL = 0.5f; // Update path every 0.5s for better responsiveness
    private static final float STOP_DISTANCE = 2.0f; // Distance at which to stop to avoid pushing target
    private final Vector2 lastTargetPos = new Vector2();

    @Override
    public Status execute() {
        Entity entity = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (steerCmp == null || steerCmp.target == null) {
            return Status.FAILED;
        }

        Box2dComponent targetB2d = ComponentMappers.box2d().get(steerCmp.target);
        if (targetB2d == null) return Status.FAILED;

        // Check distance to target
        Vector2 start = b2dCmp.body.getPosition();
        Vector2 end = targetB2d.body.getPosition();
        if (start.dst(end) < STOP_DISTANCE) {
            return Status.FAILED; // Break chase sequence and go to idle
        }

        ComponentMappers.entityType().get(entity).entityState = EntityState.walk;

        timeSinceLastPathUpdate += 0.016f; // Rough estimate of delta time

        // Update path if interval passed OR if target moved significantly OR if we have no behavior
        if (timeSinceLastPathUpdate >= PATH_UPDATE_INTERVAL || steerCmp.behavior == null || end.dst2(lastTargetPos) > 0.25f) {
            updatePathfinder(entity, b2dCmp, steerCmp, end);
            timeSinceLastPathUpdate = 0;
            lastTargetPos.set(end);
        }

        return Status.SUCCEEDED;
    }

    private void updatePathfinder(Entity entity, Box2dComponent b2dCmp, SteeringComponent steerCmp, Vector2 end) {
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        AStarPathfinder pathfinder = MapManager.getPathfinder(elevation);

        Vector2 start = b2dCmp.body.getPosition();

        Array<Vector2> waypoints = pathfinder.findPath(start, end);
        steerCmp.debugPath = waypoints;

        if (waypoints != null && waypoints.size > 1) {
            LinePath<Vector2> path = new LinePath<>(waypoints, false);
            FollowPath<Vector2, LinePath.LinePathParam> followPath = new FollowPath<>(steerCmp, path, 0.25f);
            steerCmp.behavior = createChaseBehavior(entity, steerCmp, followPath);
        } else {
            // A* failed or destination unreachable, just stop instead of seeking through walls
            steerCmp.behavior = null;
            b2dCmp.body.setLinearVelocity(0, 0);
            steerCmp.debugPath = null;
        }
    }

    private PrioritySteering<Vector2> createChaseBehavior(Entity entity, SteeringComponent steerCmp, SteeringBehavior<Vector2> followBehavior) {
        // Gather neighbors for separation
        Array<Steerable<Vector2>> neighbors = new Array<>();
        for (Entity other : EntityEngine.getInstance().getEntitiesFor(Family.all(SteeringComponent.class).get())) {
            if (other != entity) {
                neighbors.add(ComponentMappers.steering().get(other));
            }
        }
        // Reduced proximity radius for tighter, less jittery separation
        RadiusProximity<Vector2> proximity = new RadiusProximity<>(steerCmp, neighbors, 0.6f);
        Separation<Vector2> separation = new Separation<>(steerCmp, proximity);

        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<>(steerCmp);
        prioritySteering.add(separation);

        if (followBehavior != null) {
            prioritySteering.add(followBehavior);
        }
        
        return prioritySteering;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachTargetAStar();
    }
}
