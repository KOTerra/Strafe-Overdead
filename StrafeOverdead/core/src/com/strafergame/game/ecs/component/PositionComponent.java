package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.ecs.states.EntityDirection;

public class PositionComponent implements Component, Poolable {

    public Vector2 prevPos = new Vector2(0f, 0f);
    public Vector2 renderPos = new Vector2(0f, 0f);

    public int elevation=0;

    public float renderX = 0f;
    public float renderY = 0f;


    public EntityDirection direction = EntityDirection.s;
    public boolean isHidden = false;

    @Override
    public void reset() {

        this.prevPos.set(0f, 0f);
        this.renderPos.set(0f, 0f);

        this.direction = EntityDirection.s;
        this.isHidden = true;
    }
}
