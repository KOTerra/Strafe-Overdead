package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.ItemComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.world.collision.Box2DFactory;

public abstract class ItemEntityFactory {
    static EntityEngine entityEngine = EntityEngine.getInstance();

    public static Entity createItem(Entity owner, final Vector3 holdPos, float width, float height) {
        Entity item = new Entity();
        ItemComponent itmCmp = entityEngine.createComponent(ItemComponent.class);
        itmCmp.owner = owner;
        StatsComponent ownerStats = entityEngine.createComponent(StatsComponent.class);
        itmCmp.holdPosition = holdPos;
        item.add(itmCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        item.add(posCmp);

        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);
        attckCmp.owner = owner;
        attckCmp.damagePerSecond = ownerStats.meleeAttackDamagePerSecond;//modify to decide all ittem properties by itemtype
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 500;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, itmCmp.holdPosition);
        item.add(attckCmp);

        return item;
    }

    public static Vector3 inferPositionOnDirection(Entity entity) {
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        SpriteComponent spriteCmp = ComponentMappers.sprite().get(entity);

        float w = (spriteCmp != null && spriteCmp.width != 0) ? spriteCmp.width : 1f;
        float h = (spriteCmp != null && spriteCmp.height != 0) ? spriteCmp.height : 1f;

        float deg90 = 90f * MathUtils.degreesToRadians;

        return switch (posCmp.direction) {
            case a -> new Vector3(-w, h / 2, 0);
            case d -> new Vector3(w, h / 2, 0);
            case w -> new Vector3(0, h, deg90);
            case s -> new Vector3(0, -h / 2f, deg90);
        };
    }
}
