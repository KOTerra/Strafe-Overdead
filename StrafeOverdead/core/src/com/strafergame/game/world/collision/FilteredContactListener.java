package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.combat.AttackContact;
import com.strafergame.game.ecs.system.combat.ProximityContact;

public class FilteredContactListener implements ContactListener {

    public static final short HURTBOX_CATEGORY = 0x0001;                  // 0000000000000001
    public static final short HITBOX_CATEGORY = 0x0002;                   // 0000000000000010
    /**
     * collision filter for a player's own sensor of proximity
     */
    public static final short PLAYER_CATEGORY = 0x0004;                    // 0000000000000100
    /**
     * collision filter for a sensor that searches a player
     */
    public static final short PLAYER_DETECTOR_CATEGORY = 0x0008;           // 0000000000001000


    /**
     * filter for a sensor attached to the footprint
     */
    public static final short FOOTPRINT_CATEGORY = 0x0010;                 // 0000000000010000
    /**
     * filter for an elevation sensor
     */
    public static final short FOOTPRINT_DETECTOR_CATEGORY = 0x0020;        // 0000000000100000


    public static final float DETECTOR_RADIUS = 9;

    @Override
    public void beginContact(Contact contact) {
        beginFootprintContact(contact);
        beginProximityContact(contact);
        beginAttackContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        endFootprintContact(contact);
        endProximityContact(contact);
        endAttackContact(contact);
    }


    private void beginProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAPlayer = fixtureA.getFilterData().categoryBits == PLAYER_CATEGORY;
        boolean isFixtureADetector = fixtureA.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY;
        boolean isFixtureBPlayer = fixtureB.getFilterData().categoryBits == PLAYER_CATEGORY;
        boolean isFixtureBDetector = fixtureB.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY;
        solveProximity(fixtureB, fixtureA, isFixtureAPlayer, isFixtureBDetector);
        solveProximity(fixtureA, fixtureB, isFixtureADetector, isFixtureBPlayer);
    }

    private void solveProximity(Fixture fixtureA, Fixture fixtureB, boolean isFixtureADetector, boolean isFixtureBPlayer) {
        if (isFixtureADetector && isFixtureBPlayer) {
            Entity player = ComponentDataUtils.getEntityFrom(fixtureB);
            if (player != null) {
                fixtureA.setUserData(new ProximityContact(fixtureB, fixtureA));
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                if (plyrCmp != null && !plyrCmp.nearDetectors.contains(fixtureA, true)) {
                    plyrCmp.nearDetectors.add(fixtureA);
                }
            }
        }
    }

    private void endProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        removeProximity(fixtureA, fixtureB);
        removeProximity(fixtureB, fixtureA);
    }

    private void removeProximity(Fixture fixtureA, Fixture fixtureB) {
        if (fixtureA.getFilterData().categoryBits == PLAYER_DETECTOR_CATEGORY && fixtureB.getFilterData().categoryBits == PLAYER_CATEGORY) {
            fixtureA.setUserData(null);
            Entity player = ComponentDataUtils.getEntityFrom(fixtureB);
            if (player != null) {
                PlayerComponent plyrCmp = ComponentMappers.player().get(player);
                if (plyrCmp != null) {
                    plyrCmp.nearDetectors.removeValue(fixtureA, true);
                }
            }
        }
    }

    private void beginFootprintContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAFootprint = fixtureA.getFilterData().categoryBits == FOOTPRINT_CATEGORY;
        boolean isFixtureADetector = fixtureA.getFilterData().categoryBits == FOOTPRINT_DETECTOR_CATEGORY;
        boolean isFixtureBFootprint = fixtureB.getFilterData().categoryBits == FOOTPRINT_CATEGORY;
        boolean isFixtureBDetector = fixtureB.getFilterData().categoryBits == FOOTPRINT_DETECTOR_CATEGORY;

        //make proximity pair on the detector fixture.
        solveFootprint(fixtureA, fixtureB, isFixtureADetector, isFixtureBFootprint);
        solveFootprint(fixtureB, fixtureA, isFixtureBDetector, isFixtureAFootprint);
    }

    private void solveFootprint(Fixture fixtureA, Fixture fixtureB, boolean isFixtureADetector, boolean isFixtureBFootprint) {
        if (isFixtureADetector && isFixtureBFootprint) {

            fixtureA.setUserData(new ProximityContact(fixtureB, fixtureA));
            Entity detectorEntity = ComponentDataUtils.getEntityFrom(fixtureA);
            Entity footprintEntity = ComponentDataUtils.getEntityFrom(fixtureB);

            if (footprintEntity != null && detectorEntity != null) {

                ActivatorComponent actvCmp = ComponentMappers.activator().get(detectorEntity);
                if (actvCmp != null) {
                    fixtureA.setUserData(new ProximityContact(fixtureB, fixtureA));
                    Box2dComponent b2dCmp = ComponentMappers.box2d().get(footprintEntity);
                    b2dCmp.footprintStack.addFirst(detectorEntity);
                    //      System.out.println(b2dCmp.footprintStack.size());

                    ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(actvCmp.agent);
                    if (elvAgentCmp != null) {
                        elvAgentCmp.sensorBody.setAwake(true);
                    }
                }

                ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(detectorEntity);
                if (elvAgentCmp != null) {
                    elvAgentCmp.footprintBody.setAwake(false);
                    //balustrade true

                    //just the render elevation is changed, full elevation  is changed when both activators passed
                    PositionComponent positionComponent = ComponentMappers.position().get(footprintEntity);
                    if (positionComponent != null) {
                        positionComponent.elevation = elvAgentCmp.topElevation;
                        //direction, speed illusion here
                    }
                }
            }
        }
    }


    private void endFootprintContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAFootprint = fixtureA.getFilterData().categoryBits == FOOTPRINT_CATEGORY;
        boolean isFixtureADetector = fixtureA.getFilterData().categoryBits == FOOTPRINT_DETECTOR_CATEGORY;
        boolean isFixtureBFootprint = fixtureB.getFilterData().categoryBits == FOOTPRINT_CATEGORY;
        boolean isFixtureBDetector = fixtureB.getFilterData().categoryBits == FOOTPRINT_DETECTOR_CATEGORY;

        endFootprintSolveContact(fixtureB, fixtureA, isFixtureBFootprint, isFixtureADetector);
        endFootprintSolveContact(fixtureA, fixtureB, isFixtureAFootprint, isFixtureBDetector);

    }

    private void endFootprintSolveContact(Fixture fixtureA, Fixture fixtureB, boolean isFixtureAFootprint, boolean isFixtureBDetector) {
        if (isFixtureAFootprint && isFixtureBDetector) {
            Entity detectorEntity = ComponentDataUtils.getEntityFrom(fixtureB);
            Entity footprintEntity = ComponentDataUtils.getEntityFrom(fixtureA);

            if (footprintEntity != null && detectorEntity != null) {
                ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(detectorEntity);
                if (elvAgentCmp != null) {
                    elvAgentCmp.footprintBody.setAwake(true);
                    elvAgentCmp.sensorBody.setAwake(false);
                    PositionComponent posCmp = ComponentMappers.position().get(footprintEntity);
                    ElevationComponent elvCmp = ComponentMappers.elevation().get(footprintEntity);
                    //reset render elevation to real elevation
                    if (posCmp != null && elvCmp != null) {
                        posCmp.elevation = elvCmp.elevation;
                    }

                }
                fixtureB.setUserData(null);
            }
        }
    }


    private void beginAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAHurtbox = fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY;
        boolean isFixtureAHitbox = fixtureA.getFilterData().categoryBits == HITBOX_CATEGORY;
        boolean isFixtureBHurtbox = fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY;
        boolean isFixtureBHitbox = fixtureB.getFilterData().categoryBits == HITBOX_CATEGORY;

        if (isFixtureAHurtbox && isFixtureBHitbox) {
            fixtureA.setUserData(new AttackContact(fixtureA, fixtureB));
        }
        if (isFixtureAHitbox && isFixtureBHurtbox) {
            fixtureB.setUserData(new AttackContact(fixtureB, fixtureA));
        }
    }

    private void endAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (fixtureA.getFilterData().categoryBits == HURTBOX_CATEGORY && fixtureB.getFilterData().categoryBits == HITBOX_CATEGORY) {
            fixtureA.setUserData(null);
        }
        if (fixtureB.getFilterData().categoryBits == HURTBOX_CATEGORY && fixtureA.getFilterData().categoryBits == HITBOX_CATEGORY) {
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
