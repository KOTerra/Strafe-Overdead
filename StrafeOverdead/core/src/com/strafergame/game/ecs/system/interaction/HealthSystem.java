package com.strafergame.game.ecs.system.interaction;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.interaction.combat.AttackContact;
import com.strafergame.game.world.collision.Box2DWorld;
import com.strafergame.screens.GameScreen;

public class HealthSystem extends IteratingSystem {

    private boolean gameOverScheduled = false;

    public HealthSystem(Box2DWorld box2dWorld) {
        super(Family.all(HealthComponent.class, Box2dComponent.class).get());
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        HealthComponent hlthCmp = ComponentMappers.health().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);

        AttackComponent attckCmp = AttackContact.getAttack(b2dCmp);

        if (attckCmp != null) {
            if (attckCmp.owner != null && attckCmp.owner.equals(entity)) { //no self damage
                return;
            }
            typeCmp.entityState = EntityState.hit;

            System.out.println("\n\n" + hlthCmp.hitPoints + " " + attckCmp.damagePerSecond * deltaTime);
            hlthCmp.hitPoints -= attckCmp.damagePerSecond * deltaTime;
            System.out.println(hlthCmp.hitPoints);

            if (hlthCmp.hitPoints <= 0) {
                typeCmp.entityState = EntityState.death;
            }

            if (hlthCmp.hitPoints > hlthCmp.maxHitPoints) {
                hlthCmp.hitPoints = hlthCmp.maxHitPoints;
            }

        }

        //schedule death
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
                if (!gameOverScheduled) {
                    gameOverScheduled = true;
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            GameScreen.getInstance().showGameOverMenu();
                            gameOverScheduled = false;
                        }
                    }, 2f);
                }
            }
        }
    }
}
