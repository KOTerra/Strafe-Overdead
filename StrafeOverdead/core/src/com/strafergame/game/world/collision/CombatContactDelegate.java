package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.system.interaction.combat.AttackContact;

import static com.strafergame.game.world.collision.FilteredContactListener.*;
import static com.strafergame.game.world.collision.ElevationUtils.*;

public class CombatContactDelegate {

    public void beginAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short catA = fixtureA.getFilterData().categoryBits;
        short catB = fixtureB.getFilterData().categoryBits;

        // Melee/aoe Hitbox or Projectile
        short attackBits = (short) (DPS_HITBOX_CATEGORY | PROJECTILE_HITBOX_CATEGORY);

        if ((catA & HURTBOX_CATEGORY) != 0 && (catB & attackBits) != 0) {
            handleHit(fixtureA, fixtureB); // A victim, B  weapon
        } else if ((catB & HURTBOX_CATEGORY) != 0 && (catA & attackBits) != 0) {
            handleHit(fixtureB, fixtureA); // B  victim, A  weapon
        }
    }

    public void handleHit(Fixture hurtbox, Fixture hitbox) {
        // the fixture hit is actually a HURTBOX
        if ((hurtbox.getFilterData().categoryBits & HURTBOX_CATEGORY) == 0) {
            return;
        }

        if (!"jumpable".equals(hurtbox.getUserData())) {
            Entity victim = ComponentDataUtils.getEntityFrom(hurtbox);
            Object data = hitbox.getUserData();

            if (data instanceof AttackComponent attk) {
                //don t hurt the owner
                if (victim != null && victim.equals(attk.owner)) {
                    return;
                }

                hurtbox.setUserData(new AttackContact(hurtbox, hitbox));

                // Mark for removal after hitting the victim
                if ((hitbox.getFilterData().categoryBits & PROJECTILE_HITBOX_CATEGORY) != 0) {
                    attk.contactMade = true;
                }
            }
        }
    }

    public void beginProjectileWorldContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short catA = fixtureA.getFilterData().categoryBits;
        short catB = fixtureB.getFilterData().categoryBits;

        boolean isAProjectile = (catA & PROJECTILE_HITBOX_CATEGORY) != 0;
        boolean isBProjectile = (catB & PROJECTILE_HITBOX_CATEGORY) != 0;

        // wall hit if  not  a solid body
        boolean isAWall = (catA & ALL_LIGHT_BITS) != 0 && (catA & SOLID_BODY_CATEGORY) == 0;
        boolean isBWall = (catB & ALL_LIGHT_BITS) != 0 && (catB & SOLID_BODY_CATEGORY) == 0;

        if (isAProjectile && isBWall) {
            handleProjectileWallHit(fixtureA, fixtureB);
        }
        if (isBProjectile && isAWall) {
            handleProjectileWallHit(fixtureB, fixtureA);
        }
    }

    public void handleProjectileWallHit(Fixture projectile, Fixture wall) {
        Object data = projectile.getUserData();
        if (data instanceof AttackComponent attk) {
            Entity wallEntity = ComponentDataUtils.getEntityFrom(wall);

            //  actually the owner s  body, ignore
            if (wallEntity != null && wallEntity.equals(attk.owner)) {
                return;
            }

            // It's a real wall (or at least not the owner), so destroy the projectile
            //TODO check for elevation of wall to decide if contact
            attk.contactMade = true;
        }
    }

    public void endAttackContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        short hitboxBits = (short) (DPS_HITBOX_CATEGORY | PROJECTILE_HITBOX_CATEGORY);

        if ((fixtureA.getFilterData().categoryBits & HURTBOX_CATEGORY) != 0 && (fixtureB.getFilterData().categoryBits & hitboxBits) != 0) {
            // Only set to null if we actually set it to AttackContact previously
            if (fixtureA.getUserData() instanceof AttackContact) {
                fixtureA.setUserData(null);
            }
        }
        if ((fixtureB.getFilterData().categoryBits & HURTBOX_CATEGORY) != 0 && (fixtureA.getFilterData().categoryBits & hitboxBits) != 0) {
            if (fixtureB.getUserData() instanceof AttackContact) {
                fixtureB.setUserData(null);
            }
        }
    }
}
