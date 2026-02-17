package com.strafergame.game.ecs.component.world;

import box2dLight.Light;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LightComponent implements Component, Poolable {

    public final Array<LightSource> lights = new Array<>();

    public int elevation;

    @Override
    public void reset() {
        lights.clear();
        elevation = 0;
    }

    /**
     * A simple wrapper to hold a Box2D Light and its offset relative to the entity.
     */
    public static class LightSource {
        public Light light;
        public Vector2 offset;

        public LightSource(Light light, Vector2 offset) {
            this.light = light;
            this.offset = offset;
        }
    }
}