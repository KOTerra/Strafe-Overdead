package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.strafergame.game.ecs.system.combat.AttackRecoverType;
import com.strafergame.game.ecs.system.combat.AttackType;

public class AttackComponent implements Component, Poolable {

	public Body body;
	public Fixture hitbox;
	public float damagePerSecond = 0;
	public AttackType attackType;
	public AttackRecoverType attackRecoverType = AttackRecoverType.none;
	public boolean doesKnockback = false;
	public float knockbackMagnitude = 0;

	@Override
	public void reset() {
		body = null;
		hitbox = null;
		damagePerSecond = 0;
		attackRecoverType = AttackRecoverType.none;
	}

}
