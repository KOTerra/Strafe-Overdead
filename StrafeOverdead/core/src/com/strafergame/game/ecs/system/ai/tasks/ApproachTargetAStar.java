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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarPathfinder;
import com.strafergame.game.world.map.MapManager;

public class ApproachTargetAStar extends LeafTask<Entity> {

    private float timeSinceLastPathUpdate = 0;
    private static final float PATH_UPDATE_INTERVAL = 0.5f; // Update path every 0.5s for better responsiveness
    private static final float STOP_DISTANCE = 2.0f; // Distance at which to stop to avoid pushing target

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

        // Intelligent Fallback: Check Line of Sight
        World world = EntityEngine.getInstance().getBox2dWorld().getWorld();
        boolean hasLOS = hasLineOfSight(world, start, end, entity, steerCmp.target);

        if (hasLOS) {
            // If we have LOS, just use simple Seek + Separation
            steerCmp.behavior = createChaseBehavior(entity, steerCmp, null);
            steerCmp.debugPath = null;
        } else {
            // No LOS, we need A*
            if (timeSinceLastPathUpdate >= PATH_UPDATE_INTERVAL || steerCmp.debugPath == null) {
                updatePathfinder(entity, b2dCmp, steerCmp, end);
                timeSinceLastPathUpdate = 0;
            }
        }

        return Status.SUCCEEDED;
    }

    private boolean hasLineOfSight(World world, Vector2 start, Vector2 end, Entity self, Entity target) {
        final boolean[] blocked = {false};
        world.rayCast((fixture, point, normal, fraction) -> {
            if (fixture.isSensor()) return -1;
            Entity hitEntity = ComponentDataUtils.getEntityFrom(fixture);
            if (hitEntity != null && (hitEntity.equals(self) || hitEntity.equals(target))) return -1;
            
            blocked[0] = true;
            return 0;
        }, start, end);
        return !blocked[0];
    }

    private void updatePathfinder(Entity entity, Box2dComponent b2dCmp, SteeringComponent steerCmp, Vector2 end) {
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        AStarPathfinder pathfinder = MapManager.getPathfinder(elevation);

        Vector2 start = b2dCmp.body.getPosition();

        Array<Vector2> waypoints = pathfinder.findPath(start, end);
        steerCmp.debugPath = waypoints;

        if (waypoints != null && waypoints.size > 1) {
            LinePath<Vector2> path = new LinePath<>(waypoints, false);
            FollowPath<Vector2, LinePath.LinePathParam> followPath = new FollowPath<>(steerCmp, path, 1.0f);
            steerCmp.behavior = createChaseBehavior(entity, steerCmp, followPath);
        } else {
            // A* failed or destination unreachable, fallback to Seek
            steerCmp.behavior = createChaseBehavior(entity, steerCmp, null);
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
        RadiusProximity<Vector2> proximity = new RadiusProximity<>(steerCmp, neighbors, 1.2f);
        Separation<Vector2> separation = new Separation<>(steerCmp, proximity);

        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<>(steerCmp);
        prioritySteering.add(separation);

        if (followBehavior != null) {
            prioritySteering.add(followBehavior);
        } else {
            SteeringComponent targetSteer = ComponentMappers.steering().get(steerCmp.target);
            if (targetSteer != null) {
                prioritySteering.add(new Seek<>(steerCmp, targetSteer));
            }
        }
        
        return prioritySteering;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachTargetAStar();
    }
}
