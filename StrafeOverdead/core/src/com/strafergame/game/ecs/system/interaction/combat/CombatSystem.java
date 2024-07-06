package com.strafergame.game.ecs.system.interaction.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.states.EntityState;

public class CombatSystem extends IteratingSystem {

    public CombatSystem() {
        super(Family.all(Box2dComponent.class, EntityTypeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        AttackComponent attckCmp = AttackContact.getAttack(b2dCmp);
        switch (typeCmp.entityState) {
            case hit: {
                //states flow to be moved in state machines or behaviour trees so timer stuff can be programed on state entry etc
                knockback(b2dCmp, attckCmp, typeCmp);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        typeCmp.entityState = EntityState.recover;
                    }
                }, .5f);
                break;
            }
            case recover: {
                b2dCmp.body.setLinearVelocity(0f, 0f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        typeCmp.entityState = EntityState.walk;
                    }
                }, 2f);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void knockback(Box2dComponent b2dCmp, AttackComponent attckCmp, final EntityTypeComponent ettcmp) {

        if (attckCmp != null) {
            Fixture hurtbox = b2dCmp.hurtbox;

            if (hurtbox != null && attckCmp.doesKnockback) {

                Body hurtBody = hurtbox.getBody();
                Vector2 knockbackDirection = attckCmp.body.getWorldCenter().sub(hurtBody.getWorldCenter()).nor();
                knockbackDirection.scl(-attckCmp.knockbackMagnitude);

                //hurtBody.applyLinearImpulse(knockbackDirection, hurtBody.getWorldCenter(), true);
                hurtBody.applyForce(knockbackDirection, hurtBody.getWorldCenter(), true);

                return;
            }
        }

    }

}
