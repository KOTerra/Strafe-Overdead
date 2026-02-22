package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.strafergame.game.ecs.states.ItemAttachmentType;

public class ItemComponent implements Component, Pool.Poolable {
    public Entity owner;
    public Vector3 holdPosition;
    public ItemAttachmentType attachmentType = ItemAttachmentType.ATTACHED;


    @Override
    public void reset() {
        attachmentType = ItemAttachmentType.ATTACHED;
    }
}
