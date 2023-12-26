package com.strafergame.game.ecs.system.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.*;

public class ItemHoldSystem extends IteratingSystem {
    public ItemHoldSystem() {
        super(Family.all(ItemComponent.class, PositionComponent.class, AttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent itmCmp = ComponentMappers.item().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        AttackComponent attckCmp = ComponentMappers.attack().get(entity);
        PositionComponent ownerPosCmp = ComponentMappers.position().get(itmCmp.owner);
        Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(itmCmp.owner);
        ItemHolderComponent hldCmp = ComponentMappers.itemHolding().get(itmCmp.owner);


        posCmp.renderPos.x = ownerPosCmp.renderPos.x + itmCmp.holdPosition.x;
        posCmp.renderPos.y = ownerPosCmp.renderPos.y + itmCmp.holdPosition.y;
        attckCmp.hitbox.getBody().setTransform(ownerB2dCmp.body.getPosition(), 0);
    }
}
