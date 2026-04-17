package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.world.collision.Box2DMapFactory;
import com.strafergame.game.world.collision.ElevationUtils;

public class CollisionFactory implements EntityCreator {
    @Override
    public Entity create(Vector3 position, MapObject mapObject) {
        if (mapObject.getProperties().get("type") != null && (mapObject.getProperties().get("type").equals("RAILING"))) {
            return null;
        }

        EntityEngine entityEngine = EntityEngine.getInstance();
        final Entity collisionEntity = entityEngine.createEntity();
        Body body = Box2DMapFactory.createCollisionBody(entityEngine.getBox2dWorld().getWorld(), mapObject);
        body.setUserData(collisionEntity);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = mapObject.getProperties().get("elevation", 0, Integer.class);
        collisionEntity.add(elvCmp);

        ElevationUtils.setShadowFilter(body, elvCmp.elevation);

        // Add shadow casting bit for this elevation. use |= to preserve existing bits.
        short shadowBit = ElevationUtils.getWallCategory(elvCmp.elevation);
        for (Fixture f : body.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.categoryBits |= shadowBit;
            f.setFilterData(filter);
        }

        entityEngine.addEntity(collisionEntity);
        return collisionEntity;
    }
}
