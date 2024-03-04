package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.CheckpointAction;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

public class MapEntityFactory {
    private static EntityEngine entityEngine = EntityEngine.getInstance();

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


    public static Entity createCheckpoint(CheckpointAction action, final Vector2 location) {
        final Entity checkpoint = entityEngine.createEntity();
        CheckpointComponent chkCmp = entityEngine.createComponent(CheckpointComponent.class);
        chkCmp.action = action;
        checkpoint.add(chkCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.renderPos = location;
        checkpoint.add(posCmp);

        CameraComponent camCmp = entityEngine.createComponent(CameraComponent.class);
        camCmp.type = EntityType.checkpoint;
        checkpoint.add(camCmp);

        Body body = Box2DFactory.createBody(entityEngine.getBox2dWorld().getWorld(), 1f, 1f, location, BodyDef.BodyType.StaticBody);
        body.setUserData(checkpoint);
        DetectorComponent dtctrCmp = entityEngine.createComponent(DetectorComponent.class);
        dtctrCmp.detector = Box2DFactory.createSensor(body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_DETECTOR_CATEGORY, FilteredContactListener.PLAYER_CATEGORY);
        checkpoint.add(dtctrCmp);
        entityEngine.addEntity(checkpoint);

        return checkpoint;
    }

}
