package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.strafergame.game.ecs.states.EntityDirection;

import java.util.EnumMap;

public class AttachmentComponent implements Component, Pool.Poolable {

    /**
     * the entity to which this attaches, is held by, moves with it
     */
    Entity child;

    /*
     * the entity this one attaches to
     */
    Entity parent;

    /**
     * where items are held next to the owner sprite on each direction. position is relative to the body
     */
    EnumMap<EntityDirection, Vector2> childPositions;

    EnumMap<EntityDirection, Vector2> parentPositions;
    @Override
    public void reset() {

    }
}
