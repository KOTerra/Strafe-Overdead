package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarPathfinder;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.map.MapManager;

public class ApproachPlayerAStar extends LeafTask<Entity> {

    private AStarPathfinder pathfinder;
    private int lastElevation = -1;
    private float timeSinceLastPathUpdate = 0;
    private static final float PATH_UPDATE_INTERVAL = 1.0f; // Update path every second

    @Override
    public Status execute() {
        Entity entity = getObject();
        SteeringComponent steerCmp = ComponentMappers.steering().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        
        if (steerCmp == null || GameWorld.player == null) return Status.FAILED;

        if (elevation != lastElevation) {
            pathfinder = null;
            lastElevation = elevation;
        }

        ComponentMappers.entityType().get(entity).entityState = EntityState.walk;

        timeSinceLastPathUpdate += 0.016f; // Rough estimate of delta time

        if (pathfinder == null || timeSinceLastPathUpdate >= PATH_UPDATE_INTERVAL) {
            updatePathfinder(entity, b2dCmp, steerCmp);
            timeSinceLastPathUpdate = 0;
        }

        return Status.SUCCEEDED;
    }

    private void updatePathfinder(Entity entity, Box2dComponent b2dCmp, SteeringComponent steerCmp) {
        int elevation = ComponentMappers.elevation().get(entity).elevation;
        
        if (pathfinder == null) {
            pathfinder = new AStarPathfinder(MapManager.width, MapManager.height, elevation); 
        }

        Vector2 start = b2dCmp.body.getPosition();
        Vector2 end = ComponentMappers.box2d().get(GameWorld.player).body.getPosition();

        Array<Vector2> waypoints = pathfinder.findPath(start, end);

        if (waypoints != null && waypoints.size > 1) {
            LinePath<Vector2> path = new LinePath<>(waypoints, false);
            FollowPath<Vector2, LinePath.LinePathParam> followPath = new FollowPath<>(steerCmp, path, 1.0f);
            steerCmp.behavior = followPath;
        } else {
            // Fallback to simple Seek
            steerCmp.behavior = new Seek<>(steerCmp, ComponentMappers.steering().get(GameWorld.player));
        }
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task != null ? task : new ApproachPlayerAStar();
    }
}
