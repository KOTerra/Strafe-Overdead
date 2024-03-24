package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.system.combat.AttackContactPair;
import com.strafergame.game.ecs.system.combat.ProximityContactPair;

public class FilteredContactListener implements ContactListener {

    public static final short HURTBOX_CATEGORY = 0x01;
    public static final short HITBOX_CATEGORY = 2;
    /**
     * collision filter for a player's own sensor of proximity
     */
    public static final short PLAYER_CATEGORY = 3;
    /**
     * collision filter for a sensor that searches a player
     */
    public static final short PLAYER_DETECTOR_CATEGORY = 4;


    /**
     * filter for a sensor attached to the footprint
     */
    public static final short FOOTPRINT_CATEGORY = 5;
    /**
     * filter for an elevation sensor
     */
    public static final short FOOTPRINT_DETECTOR_CATEGORY = 6;

    public static final float DETECTOR_RADIUS = 9;

    @Override
    public void beginContact(Contact contact) {
        beginAttackContact(contact);
        beginProximityContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        endAttackContact(contact);
        endProximityContact(contact);
    }

    private void beginAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAHurtbox = fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY;
        boolean isFixtureAHitbox = fixtureA.getFilterData().categoryBits == HITBOX_CATEGORY;
        boolean isFixtureBHurtbox = fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY;
        boolean isFixtureBHitbox = fixtureB.getFilterData().categoryBits == HITBOX_CATEGORY;

        if (isFixtureAHurtbox && isFixtureBHitbox) {
            fixtureA.setUserData(new AttackContactPair(fixtureA, fixtureB));
        }
        if (isFixtureAHitbox && isFixtureBHurtbox) {
            fixtureB.setUserData(new AttackContactPair(fixtureB, fixtureA));
        }
    }

    private void endAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY) {
            fixtureA.setUserData(null);
        }
        if (fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY) {
            fixtureB.setUserData(null);
        }
    }

    private void beginProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAPlayer = fixtureA.getFilterData().categoryBits == PLAYER_CATEGORY;
        boolean isFixtureADetector = fixtureA.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY;
        boolean isFixtureBPlayer = fixtureB.getFilterData().categoryBits == PLAYER_CATEGORY;
        boolean isFixtureBDetector = fixtureB.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY;
        if (isFixtureAPlayer && isFixtureBDetector) {
            fixtureB.setUserData(new ProximityContactPair(fixtureA, fixtureB));
            Entity player = (Entity) fixtureA.getUserData();
            if (player != null) {
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                if (!plyrCmp.nearDetectors.contains(fixtureB, true)) {
                    plyrCmp.nearDetectors.add(fixtureB);
                }
            }
        }
        if (isFixtureADetector && isFixtureBPlayer) {
            fixtureA.setUserData(new ProximityContactPair(fixtureB, fixtureA));
            Entity player = (Entity) fixtureB.getUserData();
            if (player != null) {
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                if (!plyrCmp.nearDetectors.contains(fixtureA, true)) {
                    plyrCmp.nearDetectors.add(fixtureA);
                }
            }
        }
    }

    private void endProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY) {
            fixtureA.setUserData(null);
            Entity player = (Entity) fixtureB.getUserData();
            if (player != null) {
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                plyrCmp.nearDetectors.removeValue(fixtureA, true);
            }
        }
        if (fixtureB.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY) {
            fixtureB.setUserData(null);
            Entity player = (Entity) fixtureA.getUserData();
            if (player != null) {
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                plyrCmp.nearDetectors.removeValue(fixtureB, true);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
