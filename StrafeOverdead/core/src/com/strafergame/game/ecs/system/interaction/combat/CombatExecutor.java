package com.strafergame.game.ecs.system.interaction.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.ItemComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.factories.ItemEntityFactory;
import com.strafergame.game.ecs.states.EntityState;

public class CombatExecutor {
    public static void executeMeleeAttack(final Entity owner, final Entity meleeItem) {
        final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(owner);
        final StatsComponent statsCmp = ComponentMappers.stats().get(owner);
        final Box2dComponent b2dCmp = ComponentMappers.box2d().get(owner);
        final ItemComponent itmCmp = ComponentMappers.item().get(meleeItem);
        final AttackComponent meleeAttackCmp = ComponentMappers.attack().get(meleeItem);
        final EntityEngine entityEngine = EntityEngine.getInstance();

        typeCmp.entityState = EntityState.attack;
        typeCmp.entitySubState = EntityState.AttackSubstate.melee;


        itmCmp.holdPosition = ItemEntityFactory.inferHoldPositionOnDirection(owner);

        float targetX = b2dCmp.body.getPosition().x + itmCmp.holdPosition.x;
        float targetY = b2dCmp.body.getPosition().y + itmCmp.holdPosition.y;

        meleeAttackCmp.body.setTransform(targetX, targetY, itmCmp.holdPosition.z);
        meleeAttackCmp.body.setActive(true);

        if (!entityEngine.getEntities().contains(meleeItem, true)) {
            entityEngine.addEntity(meleeItem);
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                typeCmp.entityState = EntityState.idle;
                typeCmp.entitySubState = EntityState.NoneSubstate.none;
                meleeAttackCmp.body.setActive(false);
            }
        }, statsCmp.meleeAttackDuration);
    }

    public static void executeRangedAttack(final Entity owner, final Entity projectile) {
        final EntityTypeComponent ownerTypeCmp = ComponentMappers.entityType().get(owner);
        final StatsComponent ownerStatsCmp = ComponentMappers.stats().get(owner);
        final Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(owner);
        final ItemComponent itmCmp = ComponentMappers.item().get(projectile);
        final AttackComponent rangedAttackCmp = ComponentMappers.attack().get(projectile);
        final EntityEngine entityEngine = EntityEngine.getInstance();

        ownerTypeCmp.entityState = EntityState.attack;
        ownerTypeCmp.entitySubState = EntityState.AttackSubstate.shoot;

        itmCmp.holdPosition = ItemEntityFactory.inferHoldPositionOnDirection(owner);
        float targetX = ownerB2dCmp.body.getPosition().x + itmCmp.holdPosition.x;
        float targetY = ownerB2dCmp.body.getPosition().y + itmCmp.holdPosition.y;

        rangedAttackCmp.body.setTransform(targetX, targetY, itmCmp.holdPosition.z);
        rangedAttackCmp.body.setActive(true);
        rangedAttackCmp.body.setAwake(true);

        float speed = 15f; // m/s
        Vector2 shotDir = new Vector2(itmCmp.holdPosition.x, itmCmp.holdPosition.y).nor(); //lock mouse in the center
        // use mouse angle/rstick to determine aim dir
        // change player direction based on aim
        rangedAttackCmp.body.setLinearVelocity(shotDir.scl(speed));

        entityEngine.addEntity(projectile);


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (entityEngine.getEntities().contains(projectile, true)) {
                    entityEngine.removeEntity(projectile);
                }
            }
        }, ownerStatsCmp.rangedAttackDeletionTime);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ownerTypeCmp.entityState = EntityState.idle;
                ownerTypeCmp.entitySubState = EntityState.NoneSubstate.none;
            }
        }, ownerStatsCmp.rangedAttackDuration);
    }
}