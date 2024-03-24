package com.strafergame.game.ecs.system.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;

public class AttachmentSystem extends IteratingSystem {
    public AttachmentSystem() {
        super(Family.all(ItemComponent.class, PositionComponent.class, AttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent itmCmp = ComponentMappers.item().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        AttackComponent attckCmp = ComponentMappers.attack().get(entity);
        PositionComponent ownerPosCmp = ComponentMappers.position().get(itmCmp.owner);//parent
        Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(itmCmp.owner);
        //  ItemHolderComponent hldCmp = ComponentMappers.itemHolding().get(itmCmp.owner);
        //  to replace with AttachmentComponent
        // calculate relative positions to child and parent with appropriate attachment types and attachment positions

        posCmp.renderPos.x = ownerPosCmp.renderPos.x;
        posCmp.renderPos.y = ownerPosCmp.renderPos.y;  //+attch.
        attckCmp.hitbox.getBody().setTransform(ownerB2dCmp.body.getPosition(), 0);
    }
}
