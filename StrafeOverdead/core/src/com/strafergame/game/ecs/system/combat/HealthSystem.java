package com.strafergame.game.ecs.system.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.screens.GameScreen;

public class HealthSystem extends IteratingSystem {

    public HealthSystem(Box2DWorld box2dWorld) {
        super(Family.all(HealthComponent.class, Box2dComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        HealthComponent hlthCmp = ComponentMappers.health().get(entity);
        EntityTypeComponent ettCmp = ComponentMappers.entityType().get(entity);
        AttackComponent attckCmp = AttackContactPair.getAttack(b2dCmp);

        if (attckCmp instanceof AttackComponent) {
            if (attckCmp.owner != null && attckCmp.owner.equals(entity)) {
                return;
            }
            ettCmp.entityState = EntityState.hit;

            hlthCmp.hitPoints -= attckCmp.damagePerSecond * deltaTime;

            if (hlthCmp.hitPoints <= 0) {
                ettCmp.entityState = EntityState.death;
            }

        }
        if (ettCmp.entityState.equals(EntityState.death)) {
            if (!ettCmp.entityType.equals(EntityType.player)) {
                this.getEngine().removeEntity(entity);
            } else {
                GameScreen.getInstance().showGameOverMenu();
            }

        }

    }

}
