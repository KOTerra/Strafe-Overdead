package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.CheckpointComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.component.world.MapLayerComponent;
import com.strafergame.game.ecs.states.ElevationAgentType;
import com.strafergame.game.ecs.states.EntityDirection;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.Box2DMapFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

public class MapEntityFactory {
    private static EntityEngine entityEngine = EntityEngine.getInstance();

    public static void createLayerEntity(MapLayer layer) {
        if (layer instanceof TiledMapTileLayer) {
            PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
            SpriteComponent sprCmp = entityEngine.createComponent(SpriteComponent.class);
            MapLayerComponent layerCmp = entityEngine.createComponent(MapLayerComponent.class);
            posCmp.isMapLayer = true;
            posCmp.elevation = layer.getProperties().get("elevation", 0, Integer.class);
            sprCmp.sprite = null;
            layerCmp.layer = (TiledMapTileLayer) layer;

            entityEngine.addEntity(entityEngine.createEntity().add(posCmp).add(sprCmp).add(layerCmp));
        }
    }


    public static Entity createCollisionEntity(World world, MapObject mapObject) {

        final Entity collisionEntity = entityEngine.createEntity();
        Body body = Box2DMapFactory.createCollisionBody(world, mapObject);
        body.setUserData(collisionEntity);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = mapObject.getProperties().get("elevation", 0, Integer.class);
        collisionEntity.add(elvCmp);

        return collisionEntity;
    }

    public static Entity createElevationAgent(World world, MapObject mapObject) {
        MapProperties properties = mapObject.getProperties();

        final Entity elevationAgent = entityEngine.createEntity();

        ElevationAgentComponent elvAgentCmp = entityEngine.createComponent(ElevationAgentComponent.class);
        elvAgentCmp.sensorBody = Box2DMapFactory.createSensorBody(world, mapObject, FilteredContactListener.FOOTPRINT_DETECTOR_CATEGORY, FilteredContactListener.FOOTPRINT_CATEGORY);
        elvAgentCmp.footprintBody = Box2DMapFactory.createCollisionBody(world, properties.get("footprint", MapObject.class));
        elvAgentCmp.direction = EntityDirection.convert(properties.get("direction", String.class));
        elvAgentCmp.type = ElevationAgentType.convert(properties.get("type", String.class));
        elvAgentCmp.baseElevation = properties.get("baseElevation", Integer.class);
        elvAgentCmp.topElevation = properties.get("topElevation", Integer.class);
        elevationAgent.add(elvAgentCmp);

        elvAgentCmp.sensorBody.setUserData(elevationAgent);
        elvAgentCmp.footprintBody.setUserData(elevationAgent);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = elvAgentCmp.baseElevation;
        System.out.println((properties.get("type", String.class) + elvCmp.elevation));
        elevationAgent.add(elvCmp);

        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = elvAgentCmp.sensorBody.getFixtureList().first();
        elevationAgent.add(dtctrCmp);


        return elevationAgent;
    }

    public static Entity createCheckpoint(MapObject mapObject, CheckpointAction action) {
        final Entity checkpoint = entityEngine.createEntity();
        CheckpointComponent chkCmp = entityEngine.createComponent(CheckpointComponent.class);
        chkCmp.action = action;
        checkpoint.add(chkCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        final float x = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("x") - .5f;
        final float y = Strafer.SCALE_FACTOR * (Float) mapObject.getProperties().get("y") - .5f;
        Vector2 location = new Vector2(x, y);
        posCmp.renderPos = location;
        posCmp.elevation = mapObject.getProperties().get("elevation", 0, Integer.class);
        checkpoint.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = posCmp.elevation;

        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = EntityType.checkpoint;
        checkpoint.add(camCmp);

        Body body = Box2DFactory.createEmptyBody(entityEngine.getBox2dWorld().getWorld(), 1f, 1f, location, BodyDef.BodyType.StaticBody);
        body.setUserData(checkpoint);

        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = Box2DFactory.createRadialSensor(body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        checkpoint.add(dtctrCmp);
        entityEngine.addEntity(checkpoint);

        return checkpoint;
    }

    public static Entity createHitboxDummy(final Vector2 location, int width, int height, final Entity owner) {
        final Entity dummy = entityEngine.createEntity();
        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);

        attckCmp.owner = owner;
        attckCmp.damagePerSecond = 10;
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 5;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, 0, 0, location);
        dummy.add(attckCmp);
        entityEngine.addEntity(dummy);
        return dummy;
    }

}
