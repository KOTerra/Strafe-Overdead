package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MovementComponent implements Component, Poolable {
    public float dirX = 0f;
    public float dirY = 0f;
    public float speed = 0f;
    public float dashDuration = 0f;
    public boolean isDashCooldown = false;
    public float dashForce = 0f;

    @Override
    public void reset() {
        dirX = 0f;
        dirY = 0f;
        speed = 0f;
        dashForce = 0f;
        isDashCooldown = false;
    }

    public boolean moving() {
        return dirX != 0 || dirY != 0;
    }
}
