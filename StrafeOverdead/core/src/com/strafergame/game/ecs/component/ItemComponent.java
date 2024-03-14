package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class ItemComponent implements Component, Pool.Poolable {
    public Entity owner;
    @Override
    public void reset() {

    }
}
