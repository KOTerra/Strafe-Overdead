package com.strafergame.game.ecs.component.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool.Poolable;

import box2dLight.Light;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class Box2dComponent implements Component, Poolable {
    public boolean initiatedPhysics = false;
    public Body body;
    /**
     * the fixture situated at the base of an entity's body. it is the one who resolves collisions
     */
    public Fixture footprint;

    /**
     * a sensor attached to the footprint
     */
    public Fixture footprintSensor;

    /**
     * a stack containing previous entity contacts of the footprint sensor
     * should be cleared when an event such as en elevation is resolved, where its elements would be previous activators
     */
    public Deque<Entity> footprintStack = new ArrayDeque<>();
    public Fixture hurtbox;


    public Light light;
    public float lightDistance;
    public float lightFluctuationDistance;
    public float lightFluctuationTime;
    public float lightFluctuationSpeed;
    public float width;
    public float height;

    @Override
    public void reset() {
        initiatedPhysics = false;
        lightFluctuationDistance = 0;
        lightFluctuationTime = 0;
        lightDistance = 0;
        if (light != null) {
            light.remove(true);
            light = null;
        }
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        width = height = 0;
    }
}
