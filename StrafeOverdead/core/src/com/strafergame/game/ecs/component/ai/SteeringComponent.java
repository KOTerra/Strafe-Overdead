package com.strafergame.game.ecs.component.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;

public class SteeringComponent implements Steerable<Vector2>, Component {

    private Entity owner;
    public Entity target;

    private Box2dComponent b2dCmp;
    private MovementComponent movCmp;
    private PositionComponent posCmp;

    public SteeringBehavior<Vector2> behavior;
    public SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2());

    public SteeringComponent setOwner(Entity owner) {
        this.owner = owner;
        this.b2dCmp = ComponentMappers.box2d().get(owner);
        this.movCmp = ComponentMappers.movement().get(owner);
        this.posCmp = ComponentMappers.position().get(owner);
        return this;
    }

    public void update() {
        if (behavior != null) {
            behavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput);
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

        // Calculate velocity based on acceleration and delta time for a natural "walking" feel
        Vector2 velocity = b2dCmp.body.getLinearVelocity();
        velocity.add(steeringOutput.linear.scl(Gdx.graphics.getDeltaTime()));

        // Clamp to max speed
        velocity.limit(getMaxLinearSpeed());

        // Set velocity directly to remove the "sliding on ice" effect
        b2dCmp.body.setLinearVelocity(velocity);

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