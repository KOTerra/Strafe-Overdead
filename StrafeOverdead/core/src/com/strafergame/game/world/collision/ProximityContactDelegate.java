package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.system.interaction.ProximityContact;

import static com.strafergame.game.world.collision.FilteredContactListener.*;

public class ProximityContactDelegate {

    public void beginProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Use bitwise & instead of == to support combined bits
        boolean isFixtureAPlayer = (fixtureA.getFilterData().categoryBits & PLAYER_CATEGORY) != 0;
        boolean isFixtureADetector = (fixtureA.getFilterData().categoryBits & PLAYER_DETECTOR_CATEGORY) != 0;
        boolean isFixtureBPlayer = (fixtureB.getFilterData().categoryBits & PLAYER_CATEGORY) != 0;
        boolean isFixtureBDetector = (fixtureB.getFilterData().categoryBits & PLAYER_DETECTOR_CATEGORY) != 0;
        solveProximity(fixtureB, fixtureA, isFixtureAPlayer, isFixtureBDetector);
        solveProximity(fixtureA, fixtureB, isFixtureADetector, isFixtureBPlayer);
    }

    public void solveProximity(Fixture fixtureA, Fixture fixtureB, boolean isFixtureADetector, boolean isFixtureBPlayer) {
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

    public void endProximityContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        removeProximity(fixtureA, fixtureB);
        removeProximity(fixtureB, fixtureA);
    }

    public void removeProximity(Fixture fixtureA, Fixture fixtureB) {
        if ((fixtureA.getFilterData().categoryBits & PLAYER_DETECTOR_CATEGORY) != 0 && (fixtureB.getFilterData().categoryBits & PLAYER_CATEGORY) != 0) {
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
}
