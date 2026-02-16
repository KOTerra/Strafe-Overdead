package com.strafergame.game.ecs.component.world;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LightComponent implements Component, Poolable {
    public Light light;

    @Override
    public void reset() {
        if(light != null) {
            light.remove();
            light = null;
        }
    }
}