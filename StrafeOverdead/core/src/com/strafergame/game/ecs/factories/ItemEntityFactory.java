package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Filter;
import com.strafergame.Strafer;
import com.strafergame.assets.AnimationProvider;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.states.ItemAttachmentType;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.ElevationUtils;
import com.strafergame.game.world.collision.FilteredContactListener;
import com.sun.jdi.TypeComponent;

import static com.strafergame.game.ecs.factories.EntityFactory.attachLight;

public abstract class ItemEntityFactory {
    static EntityEngine entityEngine = EntityEngine.getInstance();

    public static Entity createItem(Entity owner, final Vector3 holdPos, float width, float height) {
        Entity item = new Entity();
        ItemComponent itmCmp = entityEngine.createComponent(ItemComponent.class);
        itmCmp.owner = owner;
        itmCmp.holdPosition = holdPos;
        item.add(itmCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        item.add(posCmp);

        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.item;
        item.add(typeCmp);


        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);
        attckCmp.owner = owner;

        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, itmCmp.holdPosition);
        item.add(attckCmp);

        return item;
    }

    public static Entity createMeleeItem(Entity owner, boolean knockback, float width, float height) {
        Entity melee = createItem(owner, inferHoldPositionOnDirection(owner), width, height);

        ItemComponent itmCmp = ComponentMappers.item().get(melee);
        AttackComponent attckCmp = ComponentMappers.attack().get(melee);
        StatsComponent ownerStats = ComponentMappers.stats().get(owner);

        itmCmp.attachmentType = ItemAttachmentType.ATTACHED;

        attckCmp.damagePerSecond = ownerStats.meleeAttackDamagePerSecond;//modify to decide all ittem properties by itemtype
        attckCmp.doesKnockback = knockback; //TODO if many melee attacks of one entity decide on list of attacks saved in stats
        attckCmp.knockbackMagnitude = ownerStats.meleeKnockbackMagnitude;

        return melee;
    }

    public static Entity createProjectile(Entity owner) {
        Entity projectile = createItem(owner, inferHoldPositionOnDirection(owner), .25f, .25f);

        ItemComponent itmCmp = ComponentMappers.item().get(projectile);
        AttackComponent attckCmp = ComponentMappers.attack().get(projectile);
        StatsComponent ownerStats = ComponentMappers.stats().get(owner);
        ElevationComponent ownerElev = ComponentMappers.elevation().get(owner);

        itmCmp.attachmentType = ItemAttachmentType.RANGE;
        attckCmp.damagePerSecond = ownerStats.rangedAttackInstantDamage;
        attckCmp.doesKnockback = false;

        Filter filter = attckCmp.hitbox.getFilterData();
        filter.categoryBits = FilteredContactListener.PROJECTILE_HITBOX_CATEGORY;

        short elevationWallBit = ElevationUtils.getWallCategory(ownerElev.elevation);

        // Mask excludes SOLID_BODY_CATEGORY, only interacts with Hurtboxes and Wall bits
        filter.maskBits = (short) (FilteredContactListener.HURTBOX_CATEGORY | elevationWallBit);

        attckCmp.hitbox.setFilterData(filter);


        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        projectile.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/dummy_static.png", Texture.class));//TODO change
        spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;

        AnimationComponent aniCmp = entityEngine.createComponent(AnimationComponent.class);
        aniCmp.animation = AnimationProvider.getAnimation(projectile);
        projectile.add(aniCmp);

        attachLight(projectile, new Vector2(0, 0), 2f, Color.RED, 32);

        return projectile;
    }

    public static Vector3 inferHoldPositionOnDirection(Entity entity) {
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