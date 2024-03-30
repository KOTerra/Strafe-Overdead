package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;

import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;

public class ElevationContactFilter implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();

        if (bypassCase(fixtureA, fixtureB)) {
            return false;
        }

        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }

        boolean collide = (filterA.maskBits & filterB.categoryBits) != 0 && (filterA.categoryBits & filterB.maskBits) != 0;
        //System.out.println(filterA.categoryBits + " " + filterB.categoryBits + " " + collide);

        if (collide) {
            // System.out.println(filterA.categoryBits + " " + filterB.categoryBits + " " + collide);
            Entity entityA = ComponentDataUtils.getEntityFrom(fixtureA);
            Entity entityB = ComponentDataUtils.getEntityFrom(fixtureB);

            if (entityA != null && entityB != null) {
                ElevationComponent elvA = ComponentMappers.elevation().get(entityA);
                ElevationComponent elvB = ComponentMappers.elevation().get(entityB);
                if (elvB != null && elvA != null) {                                             //two entities on the same elevation level
                    return (elvA.elevation == elvB.elevation);
                }

                //need to prevent activator collision if footprint is under it
                ActivatorComponent actvA = ComponentMappers.activator().get(entityA);
                ActivatorComponent actvB = ComponentMappers.activator().get(entityB);
                if (elvA != null && actvB != null) {                                                //an entity with elevation and agent components
                    ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(actvB.agent);
                    if (elvA.elevation == agentCmp.baseElevation || elvA.elevation == agentCmp.topElevation) {
                        System.err.println(fixtureB.getFilterData().categoryBits);
                        return true;
                    }
                }
                if (elvB != null && actvA != null) {
                    ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(actvA.agent);
                    if (elvB.elevation == agentCmp.baseElevation || elvB.elevation == agentCmp.topElevation) {
                        System.err.println(fixtureA.getFilterData().categoryBits);
                        return true;
                    }
                }
            }
        }
        return collide;
    }

    private boolean bypassCase(Fixture fixtureA, Fixture fixtureB) {
        if (!fixtureA.getBody().isAwake() || !fixtureB.getBody().isAwake()) {
            return true;
        }
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();

        return false;
    }
}
