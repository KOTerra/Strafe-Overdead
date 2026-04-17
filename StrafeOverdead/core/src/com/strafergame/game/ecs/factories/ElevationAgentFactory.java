package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.ActivatorType;
import com.strafergame.game.ecs.states.ElevationAgentType;
import com.strafergame.game.ecs.states.EntityDirection;
import com.strafergame.game.world.collision.Box2DMapFactory;
import com.strafergame.game.world.collision.ElevationUtils;
import com.strafergame.game.world.collision.FilteredContactListener;

public class ElevationAgentFactory implements EntityCreator {
    @Override
    public Entity create(Vector3 position, MapObject mapObject) {
        MapProperties properties = mapObject.getProperties();

        if (properties.get("type").equals("ACTIVATOR") || properties.get("type").equals("RAILING") || properties.get("type").equals("FOOTPRINT")) {
            return null;
        }

        EntityEngine entityEngine = EntityEngine.getInstance();
        World world = entityEngine.getBox2dWorld().getWorld();
        final Entity elevationAgent = entityEngine.createEntity();

        ElevationAgentComponent elvAgentCmp = entityEngine.createComponent(ElevationAgentComponent.class);
        elvAgentCmp.direction = EntityDirection.convert(properties.get("direction", String.class));
        elvAgentCmp.type = ElevationAgentType.convert(properties.get("type", String.class));
        elvAgentCmp.baseElevation = properties.get("baseElevation", Integer.class);
        elvAgentCmp.topElevation = properties.get("topElevation", Integer.class);
        elvAgentCmp.sensorBody = Box2DMapFactory.createSensorBody(world, mapObject, FilteredContactListener.FOOTPRINT_DETECTOR_CATEGORY, FilteredContactListener.FOOTPRINT_CATEGORY);
        elvAgentCmp.footprintBody = Box2DMapFactory.createCollisionBody(world, properties.get("footprint", MapObject.class));

        elvAgentCmp.baseActivator = createActivator(world, properties.get("baseActivator", MapObject.class), elevationAgent, ActivatorType.ELEVATION_UP);
        elvAgentCmp.topActivator = createActivator(world, properties.get("topActivator", MapObject.class), elevationAgent, ActivatorType.ELEVATION_DOWN);
        elvAgentCmp.leftRailing = Box2DMapFactory.createCollisionBody(world, properties.get("leftRailing", MapObject.class));
        elvAgentCmp.rightRailing = Box2DMapFactory.createCollisionBody(world, properties.get("rightRailing", MapObject.class));

        elevationAgent.add(elvAgentCmp);


        elvAgentCmp.footprintBody.setUserData(elevationAgent);
        elvAgentCmp.footprintBody.setAwake(true);
        elvAgentCmp.sensorBody.setUserData(elevationAgent);
        elvAgentCmp.sensorBody.setAwake(true);
        elvAgentCmp.leftRailing.setUserData(elevationAgent);
        elvAgentCmp.leftRailing.setAwake(true);
        elvAgentCmp.rightRailing.setUserData(elevationAgent);
        elvAgentCmp.rightRailing.setAwake(true);

        // Railings should cast shadows on the base elevation
        short shadowBit = ElevationUtils.getWallCategory(elvAgentCmp.baseElevation);
        for (Fixture f : elvAgentCmp.leftRailing.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.categoryBits |= shadowBit;
            f.setFilterData(filter);
        }
        for (Fixture f : elvAgentCmp.rightRailing.getFixtureList()) {
            Filter filter = f.getFilterData();
            filter.categoryBits |= shadowBit;
            f.setFilterData(filter);
        }


        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = elvAgentCmp.baseElevation;
        elevationAgent.add(elvCmp);

        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = elvAgentCmp.sensorBody.getFixtureList().first();
        elevationAgent.add(dtctrCmp);

        entityEngine.addEntity(elevationAgent);
        return elevationAgent;
    }

    private Entity createActivator(World world, MapObject mapObject, Entity agent, ActivatorType type) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        Entity activator = entityEngine.createEntity();

        Body body = Box2DMapFactory.createSensorBody(world, mapObject, FilteredContactListener.FOOTPRINT_DETECTOR_CATEGORY, FilteredContactListener.FOOTPRINT_CATEGORY);
        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = body.getFixtureList().first();
        activator.add(dtctrCmp);
        body.setUserData(activator);

        ActivatorComponent actvCmp = entityEngine.createComponent(ActivatorComponent.class);
        actvCmp.agent = agent;
        actvCmp.type = type;
        activator.add(actvCmp);

        entityEngine.addEntity(activator);
        return activator;
    }
}
