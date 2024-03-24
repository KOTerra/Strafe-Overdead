package com.strafergame.game.ecs.component.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements Component, Poolable {
    public Vector2 dir = new Vector2(0f, 0f);
    public float maxLinearSpeed = 7f;
    public float maxLinearAcceleration = 7f;
    public float maxAngularSpeed = 7f;
    public float maxAngularAcceleration = 7f;

    public float dashDuration = 0f;
    public boolean isDashCooldown = false;
    public float dashForce = 0f;

    @Override
    public void reset() {
        dir.set(0f, 0f);
        maxLinearSpeed = 0f;
        dashForce = 0f;
        isDashCooldown = false;
    }

    public boolean moving() {
        return dir.x != 0f || dir.y != 0f;
    }
}
