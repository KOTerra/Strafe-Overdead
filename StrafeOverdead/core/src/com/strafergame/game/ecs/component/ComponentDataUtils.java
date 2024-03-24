package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public class ComponentDataUtils {

    public static Entity getEntityFrom(Body body) {
        Entity entity = null;
        if (body != null && body.getUserData() != null) {
            if (body.getUserData() instanceof Entity cast) {
                entity = cast;
            }
        }
        return entity;
    }

    public static Entity getEntityFrom(Fixture fixture) {
        Entity entity = null;
        if (fixture != null) {
            Body body = fixture.getBody();
            entity = getEntityFrom(body);
        }
        return entity;
    }
}
