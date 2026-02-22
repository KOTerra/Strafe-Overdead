package com.strafergame.game.ecs.system.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.factories.ItemEntityFactory;
import com.strafergame.game.ecs.states.ItemAttachmentType;

/**
 * keeps item attached to owner when owner moving
 */
public class AttachmentSystem extends IteratingSystem {
    public AttachmentSystem() {
        super(Family.all(ItemComponent.class, PositionComponent.class, AttackComponent.class).get());
    }

    @Override
    protected void processEntity(Entity item, float deltaTime) {
        ItemComponent itmCmp = ComponentMappers.item().get(item);

        switch (itmCmp.attachmentType) {
            case ATTACHED -> keepAttached(item);
            case RANGE -> shoot(item);
        }


    }

    private void keepAttached(Entity item) {
        ItemComponent itmCmp = ComponentMappers.item().get(item);

        PositionComponent posCmp = ComponentMappers.position().get(item);
        AttackComponent attckCmp = ComponentMappers.attack().get(item);
        PositionComponent ownerPosCmp = ComponentMappers.position().get(itmCmp.owner);
        Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(itmCmp.owner);

        if (ownerB2dCmp != null && ownerB2dCmp.initiatedPhysics) {
            posCmp.renderPos.x = ownerPosCmp.renderPos.x + itmCmp.holdPosition.x;
            posCmp.renderPos.y = ownerPosCmp.renderPos.y + itmCmp.holdPosition.y;

            float worldX = ownerB2dCmp.body.getPosition().x + itmCmp.holdPosition.x;
            float worldY = ownerB2dCmp.body.getPosition().y + itmCmp.holdPosition.y;

            attckCmp.body.setTransform(worldX, worldY, itmCmp.holdPosition.z);
        }

    }

    private void shoot(Entity item) {

    }
}
