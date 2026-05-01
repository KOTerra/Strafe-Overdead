package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A lightweight Ashley Component that stores the articyId of an entity.
 */
public class ArticyComponent implements Component, Poolable {
    public long articyId = -1L;
    public boolean isDirty = true; // Initialized to true to ensure first-time sync

    @Override
    public void reset() {
        articyId = -1L;
        isDirty = true;
    }
}
