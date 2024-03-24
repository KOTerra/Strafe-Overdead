package com.strafergame.game.world.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;

import static com.strafergame.game.ecs.component.ComponentDataUtils.getEntityFrom;

public class ElevationContactFilter implements ContactFilter {
    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();

        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }

        boolean collide = (filterA.maskBits & filterB.categoryBits) != 0 && (filterA.categoryBits & filterB.maskBits) != 0;
        System.out.println(filterA.categoryBits + " " + filterB.categoryBits + " " + collide);

        if (collide) {
            Entity entityA = getEntityFrom(fixtureA);
            Entity entityB = getEntityFrom(fixtureB);

            if (entityA != null && entityB != null) {
                ElevationComponent elvA = ComponentMappers.elevation().get(entityA);
                ElevationComponent elvB = ComponentMappers.elevation().get(entityB);

                if (elvB != null && elvA != null) {

                    return (elvA.elevation == elvB.elevation);
                }
            }
        }
        return collide;
    }
}
