package com.strafergame.game.ecs.component.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarNode;
import com.strafergame.game.ecs.system.ai.pathfinding.AStarGraph;
import com.strafergame.game.world.map.MapManager;

public class SteeringComponent implements Steerable<Vector2>, Component {

    private Entity owner;
    public Entity target;

    private Box2dComponent b2dCmp;
    private MovementComponent movCmp;
    private PositionComponent posCmp;

    public SteeringBehavior<Vector2> behavior;
    public SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());
    public Array<Vector2> debugPath;

    public SteeringComponent setOwner(Entity owner) {
        this.owner = owner;
        this.b2dCmp = ComponentMappers.box2d().get(owner);
        this.movCmp = ComponentMappers.movement().get(owner);
        this.posCmp = ComponentMappers.position().get(owner);
        return this;
    }

    private int frameCounter = 0;
    private static final int UPDATE_INTERVAL = 5;

    public void update() {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(owner);
        if (typeCmp != null && (typeCmp.entityState == EntityState.hit || typeCmp.entityState == EntityState.death)) {
            b2dCmp.body.setLinearVelocity(0, 0);
            return;
        }

        if (behavior != null) {
            frameCounter++;
            if (frameCounter >= UPDATE_INTERVAL) {
                behavior.calculateSteering(steeringOutput);
                applySteering(steeringOutput);
                frameCounter = 0;
            }
            //change to not change direction if hit from the opposite direction

            // Only update visual direction if there is actual movement
            if (!getLinearVelocity().isZero(0.1f)) {
                posCmp.changeDirection(getLinearVelocity().nor());
            }
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steeringOutput) {
        if (steeringOutput.linear.isZero()) {
            // Stop immediately if no steering is required to prevent drifting
            b2dCmp.body.setLinearVelocity(0, 0);
            return;
        }

        Vector2 desiredVelocity = steeringOutput.linear.nor().scl(getMaxLinearSpeed());

        // Simple Wall Repulsion
        int elevation = ComponentMappers.elevation().get(owner).elevation;
        AStarGraph graph = MapManager.getPathfinder(elevation).getGraph();
        Vector2 pos = getPosition();
        int ix = (int) Math.floor(pos.x);
        int iy = (int) Math.floor(pos.y);
        Vector2 repulsion = new Vector2();
        float repulsionThreshold = 0.65f;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                AStarNode n = graph.getNode(ix + dx, iy + dy);
                if (n != null && !n.traversable) {
                    // Closest point on the wall tile's boundary to the NPC
                    float closestX = Math.max(ix + dx, Math.min(pos.x, ix + dx + 1));
                    float closestY = Math.max(iy + dy, Math.min(pos.y, iy + dy + 1));
                    Vector2 closestPoint = new Vector2(closestX, closestY);
                    Vector2 diff = pos.cpy().sub(closestPoint);
                    float dist = diff.len();
                    if (dist < repulsionThreshold) {
                        float forceScale = (repulsionThreshold - dist) / repulsionThreshold;
                        repulsion.add(diff.nor().scl(forceScale));
                    }
                }
            }
        }
        if (!repulsion.isZero()) {
            // Apply a strong enough force to steer away before physical contact
            desiredVelocity.add(repulsion.scl(getMaxLinearSpeed() * 2.5f)).nor().scl(getMaxLinearSpeed());
        }

        Vector2 currentVelocity = b2dCmp.body.getLinearVelocity();

        if (currentVelocity.isZero(0.1f)) {
            b2dCmp.body.setLinearVelocity(desiredVelocity);
        } else {
            // Smoothly rotate the current velocity towards the desired direction
            // 0.4f is fast enough to feel responsive but slow enough to remove jitter
            currentVelocity.lerp(desiredVelocity, 0.4f).nor().scl(getMaxLinearSpeed());
            b2dCmp.body.setLinearVelocity(currentVelocity);
        }

        if (steeringOutput.angular != 0) {
            b2dCmp.body.setAngularVelocity(steeringOutput.angular);
        }
    }

    public static float calculateOrientationFromLinearVelocity(Steerable<Vector2> character) {
        if (character.getLinearVelocity().isZero(character.getZeroLinearSpeedThreshold())) {
            return character.getOrientation();
        }
        return character.vectorToAngle(character.getLinearVelocity());
    }

    @Override
    public Vector2 getLinearVelocity() {
        return b2dCmp.body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return b2dCmp.body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return Math.min(b2dCmp.height, b2dCmp.width) / 2;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean b) {
    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float v) {
    }

    @Override
    public float getMaxLinearSpeed() {
        return movCmp.maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float v) {
        movCmp.maxLinearSpeed = v;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return movCmp.maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float v) {
        movCmp.maxLinearAcceleration = v;
    }

    @Override
    public float getMaxAngularSpeed() {
        return movCmp.maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float v) {
        movCmp.maxAngularSpeed = v;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return movCmp.maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float v) {
        movCmp.maxAngularAcceleration = v;
    }

    @Override
    public Vector2 getPosition() {
        return b2dCmp.body.getPosition();
    }

    @Override
    public float getOrientation() {
        return b2dCmp.body.getAngle();
    }

    @Override
    public void setOrientation(float v) {
        b2dCmp.body.setTransform(b2dCmp.body.getPosition(), v);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 vector2, float v) {
        vector2.x = -(float) Math.sin(v);
        vector2.y = (float) Math.cos(v);
        return vector2;
    }

    @Override
    public Location newLocation() {
        return null;
    }

    public Entity getOwner() {
        return owner;
    }
}
