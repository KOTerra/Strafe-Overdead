package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.system.interaction.ProximityContact;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;

import static com.strafergame.game.world.collision.FilteredContactListener.*;

public class VerticalityContactDelegate {

    public void beginFootprintContact(Contact contact) {
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

    public void solveFootprint(Fixture fixtureA, Fixture fixtureB, boolean isFixtureADetector, boolean isFixtureBFootprint) {
        if (isFixtureADetector && isFixtureBFootprint) {

            fixtureA.setUserData(new ProximityContact(fixtureB, fixtureA));
            Entity detectorEntity = ComponentDataUtils.getEntityFrom(fixtureA);
            Entity footprintEntity = ComponentDataUtils.getEntityFrom(fixtureB);

            if (footprintEntity != null && detectorEntity != null) {

                ActivatorComponent actvCmp = ComponentMappers.activator().get(detectorEntity);
                if (actvCmp != null) {
                    fixtureA.setUserData(new ProximityContact(fixtureB, fixtureA));

                    ElevationAgentComponent elvAgentCmp = ComponentMappers.elevationAgent().get(actvCmp.agent);
                    ElevationComponent elvCmp = ComponentMappers.elevation().get(footprintEntity);

                    boolean isValidActivation = false;
                    if (elvAgentCmp != null && elvCmp != null) {
                        if (detectorEntity.equals(elvAgentCmp.baseActivator)) {
                            if (elvCmp.elevation == elvAgentCmp.baseElevation || elvAgentCmp.interactingEntitites.contains(footprintEntity)) {
                                isValidActivation = true;
                            }
                        } else if (detectorEntity.equals(elvAgentCmp.topActivator)) {
                            if (elvCmp.elevation == elvAgentCmp.topElevation || elvAgentCmp.interactingEntitites.contains(footprintEntity)) {
                                isValidActivation = true;
                            }
                        }
                    }

                    if (isValidActivation) {
                        ClimbFallSystem.saveStablePosition(footprintEntity);
                        Box2dComponent b2dCmp = ComponentMappers.box2d().get(footprintEntity);
                        if (b2dCmp != null && (b2dCmp.footprintStack.isEmpty() || !b2dCmp.footprintStack.getFirst().equals(detectorEntity))) {
                            b2dCmp.footprintStack.addFirst(detectorEntity);
                        }

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

    public void endFootprintContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        boolean isFixtureAFootprint = (fixtureA.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureADetector = (fixtureA.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;
        boolean isFixtureBFootprint = (fixtureB.getFilterData().categoryBits & FOOTPRINT_CATEGORY) != 0;
        boolean isFixtureBDetector = (fixtureB.getFilterData().categoryBits & FOOTPRINT_DETECTOR_CATEGORY) != 0;

        endFootprintSolveContact(fixtureB, fixtureA, isFixtureBFootprint, isFixtureADetector);
        endFootprintSolveContact(fixtureA, fixtureB, isFixtureAFootprint, isFixtureBDetector);

    }

    public void endFootprintSolveContact(Fixture fixtureA, Fixture fixtureB, boolean isFixtureAFootprint, boolean isFixtureBDetector) { //setEnaabled railing, elevationfootprint if they not awake? to evade the case when they are still colliding
        if (isFixtureAFootprint && isFixtureBDetector) {
            Entity detectorEntity = ComponentDataUtils.getEntityFrom(fixtureB);
            Entity footprintEntity = ComponentDataUtils.getEntityFrom(fixtureA);

            if (footprintEntity != null && detectorEntity != null) {
                // If it was an activator, save position
                if (ComponentMappers.activator().has(detectorEntity)) {
                    ClimbFallSystem.saveStablePosition(footprintEntity);
                }

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

    public void addInteractingEntity(ElevationAgentComponent elvAgentCmp, Entity entity) {
        ElevationUtils.changeRenderElevation(entity, elvAgentCmp.topElevation);
        if (!elvAgentCmp.interactingEntitites.contains(entity)) {
            elvAgentCmp.interactingEntitites.add(entity);
            ComponentMappers.elevation().get(entity).isClimbing = true;
        }
    }

    public void removeInteractingEntity(ElevationAgentComponent elvAgentCmp, Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (elvCmp != null) {
            ElevationUtils.changeRenderElevation(entity, elvCmp.elevation);
            elvCmp.isClimbing = false;
        }

        elvAgentCmp.interactingEntitites.remove(entity);
    }
}
