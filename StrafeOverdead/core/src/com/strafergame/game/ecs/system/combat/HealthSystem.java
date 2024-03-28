package com.strafergame.game.ecs.system.combat;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
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
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        AttackComponent attckCmp = AttackContact.getAttack(b2dCmp);

        if (attckCmp instanceof AttackComponent) {
            if (attckCmp.owner != null && attckCmp.owner.equals(entity)) {
                return;
            }
            typeCmp.entityState = EntityState.hit;

            hlthCmp.hitPoints -= attckCmp.damagePerSecond * deltaTime;

            if (hlthCmp.hitPoints <= 0) {
                typeCmp.entityState = EntityState.death;
            }

        }
        if (typeCmp.entityState.equals(EntityState.death)) {
            if (!typeCmp.entityType.equals(EntityType.player)) {
                b2dCmp.body.setLinearVelocity(0f, 0f);
                Engine engine = this.getEngine();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        engine.removeEntity(entity);
                    }
                }, 2f);

            } else {
                GameScreen.getInstance().showGameOverMenu();
            }

        }

    }

}
