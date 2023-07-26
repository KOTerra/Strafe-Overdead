package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.ecs.states.EntityDirection;

public class PositionComponent implements Component, Poolable {
    public float prevX = 0f;
    public float prevY = 0f;
    public float renderX = 0f;
    public float renderY = 0f;
    public EntityDirection direction = EntityDirection.s;
    public boolean isHidden = false;

    @Override
    public void reset() {

        this.prevX = 0f;
        this.prevY = 0f;
        this.renderX = 0f;
        this.renderY = 0f;
        this.direction = EntityDirection.s;
        this.isHidden = true;
    }
}
