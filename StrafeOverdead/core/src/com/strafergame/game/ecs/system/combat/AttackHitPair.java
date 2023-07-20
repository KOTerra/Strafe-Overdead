package com.strafergame.game.ecs.system.combat;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.Box2dComponent;

public class AttackHitPair {
	public Fixture hitbox;
	public Fixture hurtbox;

	public AttackHitPair(Fixture hurtbox, Fixture hitbox) {

		this.hitbox = hitbox;
		this.hurtbox = hurtbox;
	}

	public AttackComponent getAttack() {
		return (AttackComponent) hitbox.getUserData();
	}

	public static AttackComponent getAttack(Box2dComponent b2dCmp) {
		AttackHitPair pair = (AttackHitPair) b2dCmp.hurtbox.getUserData();
		if (pair != null) {
			AttackComponent attckCmp = (AttackComponent) pair.hitbox.getUserData();
			return attckCmp;
		}
		return null;
	}
}
