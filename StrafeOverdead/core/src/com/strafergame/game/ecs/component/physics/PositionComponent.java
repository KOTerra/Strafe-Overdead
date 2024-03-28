package com.strafergame.game.ecs.component.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.ecs.states.EntityDirection;

public class PositionComponent implements Component, Poolable {

    public Vector2 prevPos = new Vector2(0f, 0f);
    public Vector2 renderPos = new Vector2(0f, 0f);

    public int elevation = 0;


    public EntityDirection direction = EntityDirection.s;
    public boolean isHidden = false;

    public boolean isMapLayer = false;

    public void changeDirection(Vector2 dir) {
        if (dir.y > .25f) {
            direction = EntityDirection.w;
        }
        if (dir.y < -.25f) {
            direction = EntityDirection.s;
        }
        if (dir.x < -.5f) {
            direction = EntityDirection.a;
        }
        if (dir.x > .5f) {
            direction = EntityDirection.d;
        }
    }

    @Override
    public void reset() {

        this.prevPos.set(0f, 0f);
        this.renderPos.set(0f, 0f);

        this.direction = EntityDirection.s;
        this.isHidden = true;
        this.isMapLayer = false;
    }
}
