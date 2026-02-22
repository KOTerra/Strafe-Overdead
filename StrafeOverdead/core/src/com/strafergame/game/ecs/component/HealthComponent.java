package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HealthComponent implements Component, Poolable {
    public float maxHitPoints = 0;
    public float hitPoints = 0;

    public void init(float max) {
        maxHitPoints = max;
        hitPoints = max;
    }

    @Override
    public void reset() {
        maxHitPoints = 0;
        hitPoints = 0;
    }
}
