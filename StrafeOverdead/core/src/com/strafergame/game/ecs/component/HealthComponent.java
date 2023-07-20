package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HealthComponent implements Component, Poolable {
    int hitPoints=0;

    @Override
    public void reset() {
        hitPoints=0;
    }
}
