package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.strafergame.game.ecs.states.EntityDirection;

import java.util.EnumMap;

public class ItemHolderComponent implements Component, Pool.Poolable {
    Array<Entity> items = new Array<>();

    /**
     * where items are held next to the owner sprite on each direction. position is relative to the body
     */
    EnumMap<EntityDirection, Vector2>  holdPositions;
    @Override

    public void reset() {

    }
}
