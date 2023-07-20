package com.strafergame.game.world.collision;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strafergame.game.ecs.system.combat.AttackHitPair;

public class FilteredContactListener implements ContactListener {

	public static final short HURTBOX_CATEGORY = 1;
	public static final short HITBOX_CATEGORY = 2;

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		boolean isFixtureAHurtbox = fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY;
		boolean isFixtureAHitbox = fixtureA.getFilterData().categoryBits == HITBOX_CATEGORY;
		boolean isFixtureBHurtbox = fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY;
		boolean isFixtureBHitbox = fixtureB.getFilterData().categoryBits == HITBOX_CATEGORY;

		if (isFixtureAHurtbox && isFixtureBHitbox) {
			fixtureA.setUserData(new AttackHitPair(fixtureA, fixtureB));

		}
		if (isFixtureAHitbox && isFixtureBHurtbox) {
			fixtureB.setUserData(new AttackHitPair(fixtureB, fixtureA));
		}

	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY) {
			fixtureA.setUserData(null);
		}
		if (fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY) {
			fixtureB.setUserData(null);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

}
