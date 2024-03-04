package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.ItemComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.world.collision.Box2DFactory;

public abstract class ItemEntityFactory {
    static EntityEngine entityEngine = EntityEngine.getInstance();

    public static Entity createItem(Entity owner, final Vector2 holdPos, int width, int height) {
        Entity item = new Entity();
        ItemComponent itmCmp = entityEngine.createComponent(ItemComponent.class);
        itmCmp.owner = owner;
        itmCmp.holdPosition = holdPos;
        item.add(itmCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        item.add(posCmp);

        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);
        attckCmp.owner = owner;
        attckCmp.damagePerSecond = 40;
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 5;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, 0, 0, holdPos);
        item.add(attckCmp);

        return item;
    }
}
