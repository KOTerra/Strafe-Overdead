package com.strafergame.game.ecs.system.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.states.EntityState;

public class CombatSystem extends IteratingSystem {

	public CombatSystem() {
		super(Family.all(Box2dComponent.class, EntityTypeComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
		EntityTypeComponent ettCmp = ComponentMappers.entityType().get(entity);
		AttackComponent attckCmp = AttackContactPair.getAttack(b2dCmp);
		switch (ettCmp.entityState) {
		case hit: {
			knockback(b2dCmp, attckCmp, ettCmp);
			break;
		}
		case recover: {
			break;
		}
		default: {
			break;
		}
		}
	}

	private void knockback(Box2dComponent b2dCmp, AttackComponent attckCmp, final EntityTypeComponent ettcmp) {

		if (attckCmp != null) {
			if (attckCmp instanceof AttackComponent) {
				Fixture hurt = b2dCmp.hurtbox;

				if (hurt != null && attckCmp.doesKnockback) {

					Body hurtBody = hurt.getBody();
					Vector2 knockbackDirection = attckCmp.body.getWorldCenter().sub(hurtBody.getWorldCenter()).nor();
					knockbackDirection.scl(attckCmp.knockbackMagnitude);
					knockbackDirection.scl(-1);

					hurtBody.applyLinearImpulse(knockbackDirection, hurtBody.getWorldCenter(), true);
					return;
				}
			}
		}

		ettcmp.entityState = EntityState.idle;

	}

}
