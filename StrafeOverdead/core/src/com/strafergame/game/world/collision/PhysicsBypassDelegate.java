package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;

public class PhysicsBypassDelegate {

    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // if one is a jumping/falling entity and the other is a jumpable fixture disable contact
        if (checkJumpBypass(contact, fixtureA, fixtureB) || checkJumpBypass(contact, fixtureB, fixtureA)) {
            contact.setEnabled(false); //
        }

        checkElevationAgentBypass(contact, fixtureA, fixtureB);
        checkElevationAgentBypass(contact, fixtureB, fixtureA);
    }

    public void checkElevationAgentBypass(Contact contact, Fixture agentFixture, Fixture entityFixture) {
        Entity agentEntity = ComponentDataUtils.getEntityFrom(agentFixture);
        if (agentEntity == null) {
            return;
        }
        ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(agentEntity);
        if (elvAgentCmp == null) {
            return;
        }

        Entity entity = ComponentDataUtils.getEntityFrom(entityFixture);
        if (entity == null) {
            return;
        }

        boolean isInteracting = elvAgentCmp.interactingEntitites.contains(entity);
        Body agentBody = agentFixture.getBody();

        if (agentBody == elvAgentCmp.footprintBody) {
            if (isInteracting) {
                contact.setEnabled(false);
            }
        } else if (agentBody == elvAgentCmp.leftRailing || agentBody == elvAgentCmp.rightRailing) {
            if (!isInteracting) {
                contact.setEnabled(false);
            }
        }
    }

    public boolean checkJumpBypass(Contact contact, Fixture entityFixture, Fixture wallFixture) {
        Entity entity = ComponentDataUtils.getEntityFrom(entityFixture);
        if (entity == null) return false;

        boolean isJumpable = ClimbFallSystem.JUMPABLE_TAG.equals(wallFixture.getUserData());

        if (isJumpable) {
            contact.setFriction(0f);
        }

        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);

        boolean isBypassing = typeCmp != null &&
                (typeCmp.entityState == EntityState.jump || typeCmp.entityState == EntityState.fall || typeCmp.entityState == EntityState.dash);


        return isBypassing && isJumpable;
    }
}
