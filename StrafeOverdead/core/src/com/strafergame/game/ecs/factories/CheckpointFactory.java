package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.CheckpointComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;
import com.strafergame.graphics.ColorPallete;

public class CheckpointFactory implements EntityCreator {
    @Override
    public Entity create(Vector3 position, MapObject mapObject) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        final Entity checkpoint = entityEngine.createEntity();
        CheckpointComponent chkCmp = entityEngine.createComponent(CheckpointComponent.class);
        chkCmp.action = () -> {
        };
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
        checkpoint.add(elvCmp);

        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = EntityType.checkpoint;
        checkpoint.add(camCmp);

        Body body = Box2DFactory.createEmptyBody(entityEngine.getBox2dWorld().getWorld(), 1f, 1f, location, BodyDef.BodyType.StaticBody);
        body.setUserData(checkpoint);

        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = Box2DFactory.createRadialSensor(body, FilteredContactListener.DETECTOR_RADIUS, FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        checkpoint.add(dtctrCmp);

        EntityFactory.attachLight(checkpoint, new Vector2(2.5f, 2.5f), 15f, ColorPallete.MAGENTA_LIGHT_COLOR, 128);
        EntityFactory.attachLight(checkpoint, new Vector2(-2.5f, -2.5f), 15f, ColorPallete.CYAN_LIGHT_COLOR, 128);

        entityEngine.addEntity(checkpoint);

        return checkpoint;
    }
}
