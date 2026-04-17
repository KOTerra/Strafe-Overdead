package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.interaction.combat.AttackContact;
import com.strafergame.game.ecs.system.interaction.ProximityContact;

public class FilteredContactListener implements ContactListener {

    public static final short HURTBOX_CATEGORY = 0x0001;                  // 1
    public static final short DPS_HITBOX_CATEGORY = 0x0002;                   // 2
    /**
     * collision filter for a player's own sensor of proximity
     */
    public static final short PLAYER_CATEGORY = 0x0004;                    // 4
    /**
     * collision filter for a sensor that searches a player
     */
    public static final short PLAYER_DETECTOR_CATEGORY = 0x0008;           // 8


    /**
     * filter for a sensor attached to the footprint
     */
    public static final short FOOTPRINT_CATEGORY = 0x0010;                 // 16
    /**
     * filter for an elevation sensor
     */
    public static final short FOOTPRINT_DETECTOR_CATEGORY = 0x0020;        // 32

    /**
     * filter for projectiles
     */
    public static final short PROJECTILE_HITBOX_CATEGORY = 0x0040;                // 64

    public static final short SOLID_BODY_CATEGORY = 0x0080; // 128

    // --- LIGHTING BITS (Reserved Bits 8-15) ---
    public static final short LIGHT_BIT_OFFSET = 8;
    public static final short ALL_LIGHT_BITS = (short) 0xFF00;

    public static short getWallCategory(int elevation) {
        int bitShift = (elevation % 8) + LIGHT_BIT_OFFSET;
        return (short) (1 << bitShift);
    }

    /**
     * Updates fixture filters to cast shadows on a specific elevation
     * without losing existing gameplay categories (Player, Footprint, etc).
     */
    public static void setShadowFilter(Body body, int elevation) {
        if (body == null) return;
        short shadowBit = getWallCategory(elevation);
        for (Fixture fixture : body.getFixtureList()) {
            // Only solid objects (non-sensors) cast shadows
            if (!fixture.isSensor()) {
                Filter filter = fixture.getFilterData();
                filter.categoryBits &= ~ALL_LIGHT_BITS; // Clear old elevation bits
                filter.categoryBits |= shadowBit;       // Add current elevation bit //TODO also use for shooting on different elevations
                fixture.setFilterData(filter);
            }
        }
    }


    public static final float DETECTOR_RADIUS = 9;

    @Override
    public void beginContact(Contact contact) {
        beginFootprintContact(contact);
        beginProximityContact(contact);
        beginAttackContact(contact);
        beginProjectileWorldContact(contact);
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

        // Use bitwise & instead of == to support combined bits
        boolean isFixtureAPlayer = (fixtureA.getFilterData().categoryBits & PLAYER_CATEGORY) != 0;
        boolean isFixtureADetector = (fixtureA.getFilterData().categoryBits & PLAYER_DETECTOR_CATEGORY) != 0;
        boolean isFixtureBPlayer = (fixtureB.getFilterData().categoryBits & PLAYER_CATEGORY) != 0;
        boolean isFixtureBDetector = (fixtureB.getFilterData().categoryBits & PLAYER_DETECTOR_CATEGORY) != 0;
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

    private void beginFootprintContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAFootprint = (fixtureA.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureADetector = (fixtureA.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;
        boolean isFixtureBFootprint = (fixtureB.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureBDetector = (fixtureB.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;

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
                    if (b2dCmp != null && (b2dCmp.footprintStack.isEmpty() || !b2dCmp.footprintStack.getFirst().equals(detectorEntity))) {
                        b2dCmp.footprintStack.addFirst(detectorEntity);
                    }

                    ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(actvCmp.agent);
                    if (elvAgentCmp != null) {
                        elvAgentCmp.sensorEnabledEntities.add(footprintEntity);
                        // Check if the entity is already in the slope area
                        if (elvAgentCmp.inSlopeArea.contains(footprintEntity)) {
                            addInteractingEntity(elvAgentCmp, footprintEntity);
                        }
                    }
                }

                ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(detectorEntity);
                if (elvAgentCmp != null) {
                    if (!elvAgentCmp.inSlopeArea.contains(footprintEntity)) {
                        elvAgentCmp.inSlopeArea.add(footprintEntity);
                    }

                    if (elvAgentCmp.sensorEnabledEntities.contains(footprintEntity)) {
                        addInteractingEntity(elvAgentCmp, footprintEntity);
                    }
                }
            }
        }
    }


    private void endFootprintContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAFootprint = (fixtureA.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureADetector = (fixtureA.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;
        boolean isFixtureBFootprint = (fixtureB.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureBDetector = (fixtureB.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;

        endFootprintSolveContact(fixtureB, fixtureA, isFixtureBFootprint, isFixtureADetector);
        endFootprintSolveContact(fixtureA, fixtureB, isFixtureAFootprint, isFixtureBDetector);

    }

    private void endFootprintSolveContact(Fixture fixtureA, Fixture fixtureB, boolean isFixtureAFootprint, boolean isFixtureBDetector) { //setEnaabled railing, elevationfootprint if they not awake? to evade the case when they are still colliding
        if (isFixtureAFootprint && isFixtureBDetector) {
            Entity detectorEntity = ComponentDataUtils.getEntityFrom(fixtureB);
            Entity footprintEntity = ComponentDataUtils.getEntityFrom(fixtureA);

            if (footprintEntity != null && detectorEntity != null) {
                ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(detectorEntity);
                if (elvAgentCmp != null) {
                    elvAgentCmp.inSlopeArea.remove(footprintEntity);
                    elvAgentCmp.sensorEnabledEntities.remove(footprintEntity);
                    removeInteractingEntity(elvAgentCmp, footprintEntity);
                }
                fixtureB.setUserData(null);
            }
        }
    }

    private void addInteractingEntity(ElevationAgentComponent elvAgentCmp, Entity entity) {
        //just the render elevation is changed, full elevation  is changed when both activators passed
        PositionComponent positionComponent = ComponentMappers.position().get(entity);
        if (positionComponent != null) {
            positionComponent.elevation = elvAgentCmp.topElevation;
            // Update shadow bit to cast shadows on the upper layer
            setShadowFilter(ComponentMappers.box2d().get(entity).body, positionComponent.elevation);
        }
        if (!elvAgentCmp.interactingEntitites.contains(entity)) {
            elvAgentCmp.interactingEntitites.add(entity);
            ComponentMappers.elevation().get(entity).isClimbing = true;
        }
    }

    private void removeInteractingEntity(ElevationAgentComponent elvAgentCmp, Entity entity) {
        PositionComponent posCmp = ComponentMappers.position().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        //reset render elevation to real elevation
        if (posCmp != null && elvCmp != null) {
            posCmp.elevation = elvCmp.elevation;
            // Restore shadow bit to original layer
            setShadowFilter(ComponentMappers.box2d().get(entity).body, posCmp.elevation);
        }

        elvAgentCmp.interactingEntitites.remove(entity);
        if (elvCmp != null) {
            elvCmp.isClimbing = false;
        }
    }


    private void beginAttackContact(Contact contact) {
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

    private void handleHit(Fixture hurtbox, Fixture hitbox) {
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

    private void beginProjectileWorldContact(Contact contact) {
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

    private void handleProjectileWallHit(Fixture projectile, Fixture wall) {
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


    private void endAttackContact(Contact contact) {
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

    @Override
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

    private void checkElevationAgentBypass(Contact contact, Fixture agentFixture, Fixture entityFixture) {
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

    private boolean checkJumpBypass(Contact contact, Fixture entityFixture, Fixture wallFixture) {
        Entity entity = ComponentDataUtils.getEntityFrom(entityFixture);
        if (entity == null) return false;

        boolean isJumpable = "jumpable".equals(wallFixture.getUserData());

        if (isJumpable) {
            contact.setFriction(0f);
        }

        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);

        boolean isBypassing = typeCmp != null &&
                (typeCmp.entityState == EntityState.jump || typeCmp.entityState == EntityState.fall || typeCmp.entityState == EntityState.dash);


        return isBypassing && isJumpable;
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}