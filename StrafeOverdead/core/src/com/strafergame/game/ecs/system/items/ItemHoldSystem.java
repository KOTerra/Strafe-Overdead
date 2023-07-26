package com.strafergame.game.ecs.system.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.ItemComponent;
import com.strafergame.game.ecs.component.PositionComponent;

public class ItemHoldSystem extends IteratingSystem {
    public ItemHoldSystem() {
        super(Family.all(ItemComponent.class, PositionComponent.class, AttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ItemComponent itmCmp = ComponentMappers.item().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        PositionComponent ownerPosCmp = ComponentMappers.position().get(itmCmp.owner);
        AttackComponent attckCmp=ComponentMappers.attack().get(entity);
        Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(itmCmp.owner);
        posCmp.renderX = ownerPosCmp.renderX + itmCmp.holdPosition.x;
        posCmp.renderY = ownerPosCmp.renderY + itmCmp.holdPosition.y;
        attckCmp.hitbox.getBody().setTransform(ownerB2dCmp.body.getPosition(), 0);

    }
}
