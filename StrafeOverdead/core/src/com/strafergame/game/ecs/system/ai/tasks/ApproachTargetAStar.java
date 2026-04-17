package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarPathfinder;
import com.strafergame.game.world.map.MapManager;

public class ApproachTargetAStar extends LeafTask<Entity> {

    private float timeSinceLastPathUpdate = 0;
    private static final float PATH_UPDATE_INTERVAL = 0.5f; // Update path every 0.5s for better responsiveness
    private static final float STOP_DISTANCE = 2.0f; // Distance at which to stop to avoid pushing target
    private static final float PRECISION_DISTANCE = 0.8f; // Radius for direct, no-separation steering
    private final Vector2 lastTargetPos = new Vector2();

    @Override
    public Status execute() {
        Entity entity = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);

        if (steerCmp == null || steerCmp.target == null) {
            return Status.FAILED;
        }

        Box2dComponent targetB2d = ComponentMappers.box2d().get(steerCmp.target);
        ElevationComponent targetElvCmp = ComponentMappers.elevation().get(steerCmp.target);
        if (targetB2d == null || targetElvCmp == null) return Status.FAILED;

        int currentElevation = elvCmp.elevation;
        int targetElevation = targetElvCmp.elevation;

        Vector2 start = b2dCmp.body.getPosition();
        Vector2 end;
        boolean useSeparation = true;

        if (currentElevation == targetElevation) {
            // Phase: CHASE (standard A*)
            end = targetB2d.body.getPosition();
            if (start.dst(end) < STOP_DISTANCE && !elvCmp.isClimbing) {
                return Status.FAILED; // Break chase sequence and go to idle
            }
            if (elvCmp.isClimbing) {
                // Just reached the correct level, but still on the slope sensor.
                // Seek the player directly to get off the slope without separation issues.
                useSeparation = false;
            }
        } else if (elvCmp.isClimbing) {
            // Phase: CLIMBING (precision interpolation)
            // We are on a slope and elevations don't match, seek the OTHER end of the slope.
            Vector2 otherActivatorPos = getOtherActivatorPosition(entity);
            if (otherActivatorPos != null) {
                end = otherActivatorPos;
                useSeparation = false;
            } else {
                return Status.FAILED;
            }
        } else {
            // Phase: PREP / ENTRY
            // Elevations don't match and we aren't climbing yet.
            // Priority 1: Target player's last stable position IF it is on our current elevation.
            if (targetElvCmp.lastStableElevation == currentElevation && targetElvCmp.lastStablePosition != null) {
                end = targetElvCmp.lastStablePosition;
            } else {
                // Priority 2: Target the nearest slope that connects our level to the player's level.
                end = getNearestConnectingActivator(entity, currentElevation, targetElevation);
            }

            if (end == null) {
                steerCmp.behavior = null;
                b2dCmp.body.setLinearVelocity(0, 0);
                return Status.FAILED;
            }

            // If close to activator/stable pos, switch to precision mode to ensure physical contact
            if (start.dst(end) < PRECISION_DISTANCE) {
                useSeparation = false;
            }
        }

        ComponentMappers.entityType().get(entity).entityState = EntityState.walk;

        timeSinceLastPathUpdate += 0.016f; // Rough estimate of delta time

        // Update path if interval passed OR if target moved significantly OR if we have no behavior
        if (timeSinceLastPathUpdate >= PATH_UPDATE_INTERVAL || steerCmp.behavior == null || end.dst2(lastTargetPos) > 0.25f) {
            updatePathfinder(entity, b2dCmp, steerCmp, end, useSeparation);
            timeSinceLastPathUpdate = 0;
            lastTargetPos.set(end);
        }

        return Status.SUCCEEDED;
    }

    private void updatePathfinder(Entity entity, Box2dComponent b2dCmp, SteeringComponent steerCmp, Vector2 end, boolean useSeparation) {
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        AStarPathfinder pathfinder = MapManager.getPathfinder(elevation);

        Vector2 start = b2dCmp.body.getPosition();

        Array<Vector2> waypoints;
        if (!useSeparation) {
            // Precision interpolation mode: direct line to ensure contact
            waypoints = new Array<>();
            waypoints.add(start);
            waypoints.add(end);
        } else {
            waypoints = pathfinder.findPath(start, end);
            // Fallback to target's last stable position if direct path fails
            if (waypoints == null && steerCmp.target != null) {
                ElevationComponent targetElvCmp = ComponentMappers.elevation().get(steerCmp.target);
                if (targetElvCmp != null && targetElvCmp.lastStablePosition != null && targetElvCmp.lastStableElevation == elevation) {
                    waypoints = pathfinder.findPath(start, targetElvCmp.lastStablePosition);
                }
            }
        }

        steerCmp.debugPath = waypoints;

        if (waypoints != null && waypoints.size > 1) {
            LinePath<Vector2> path = new LinePath<>(waypoints, false);
            FollowPath<Vector2, LinePath.LinePathParam> followPath = new FollowPath<>(steerCmp, path, useSeparation ? 0.25f : 0.1f);
            if (useSeparation) {
                steerCmp.behavior = createChaseBehavior(entity, steerCmp, followPath);
            } else {
                steerCmp.behavior = followPath; // No PrioritySteering/Separation
            }
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

    private Vector2 getOtherActivatorPosition(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        if (b2dCmp == null || b2dCmp.footprintStack.isEmpty()) return null;

        Entity firstActivator = b2dCmp.footprintStack.getFirst();
        ActivatorComponent actvCmp = ComponentMappers.activator().get(firstActivator);
        if (actvCmp == null) return null;

        ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(actvCmp.agent);
        if (agentCmp == null) return null;

        Entity targetActivator = null;
        if (firstActivator.equals(agentCmp.baseActivator)) {
            targetActivator = agentCmp.topActivator;
        } else if (firstActivator.equals(agentCmp.topActivator)) {
            targetActivator = agentCmp.baseActivator;
        }

        if (targetActivator != null) {
            DetectorComponent dtctr = ComponentMappers.detector().get(targetActivator);
            if (dtctr != null && dtctr.detector != null) {
                return dtctr.detector.getBody().getPosition();
            }
        }
        return null;
    }

    private Vector2 getNearestConnectingActivator(Entity entity, int currentElevation, int targetElevation) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        Vector2 pos = b2dCmp.body.getPosition();

        Vector2 bestActivatorPos = null;
        float bestDist = Float.MAX_VALUE;

        for (Entity agentEntity : EntityEngine.getInstance().getEntitiesFor(Family.all(ElevationAgentComponent.class).get())) {
            ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(agentEntity);

            boolean canGoUp = (currentElevation == agentCmp.baseElevation && targetElevation >= agentCmp.topElevation);
            boolean canGoDown = (currentElevation == agentCmp.topElevation && targetElevation <= agentCmp.baseElevation);

            Entity targetActivator = null;
            if (canGoUp) {
                targetActivator = agentCmp.baseActivator;
            } else if (canGoDown) {
                targetActivator = agentCmp.topActivator;
            }

            if (targetActivator != null) {
                DetectorComponent dtctr = ComponentMappers.detector().get(targetActivator);
                if (dtctr != null && dtctr.detector != null) {
                    Vector2 actPos = dtctr.detector.getBody().getPosition();
                    float dst = pos.dst2(actPos);
                    if (dst < bestDist) {
                        bestDist = dst;
                        bestActivatorPos = actPos;
                    }
                }
            }
        }

        return bestActivatorPos;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachTargetAStar();
    }
}
