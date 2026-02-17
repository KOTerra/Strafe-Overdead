package com.strafergame.game.ecs.component.world;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LightComponent implements Component, Poolable {
    public Light light;
    public int elevation =0;
    public Vector2 offset = new Vector2();

    @Override
    public void reset() {
        if (light != null) {
            light.remove();
            light = null;
        }
    }
}