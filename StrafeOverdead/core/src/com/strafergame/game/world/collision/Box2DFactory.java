package com.strafergame.game.world.collision;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;

public abstract class Box2DFactory {

    public static Body createBody(Box2dComponent b2dCmp, World world, float width, float height, float xOffset,
                                  float yOffset, Vector2 pos, BodyDef.BodyType type) {
        Body body;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((pos.x + width / 2) + xOffset, (pos.y + height / 2) + yOffset);
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;
        bodyDef.type = type;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        // PolygonShape boxShape = new PolygonShape();
        // boxShape.setAsBox(width / 2, height / 2);
        CircleShape boxShape = new CircleShape();
        boxShape.setRadius(width / 2);
        fixtureDef.shape = boxShape;
        fixtureDef.restitution = 0;

        Fixture fingerprint = body.createFixture(fixtureDef);
        b2dCmp.fingerprint = fingerprint;
        b2dCmp.body = body;
        boxShape.dispose();

        return body;
    }

    public static Body createBody(World world, float width, float height, Vector2 pos, BodyDef.BodyType type) {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((pos.x + width / 2), (pos.y + height / 2));
        bodyDef.fixedRotation = true;
        bodyDef.type = type;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(width / 2, height / 2);
        fixtureDef.shape = boxShape;
        fixtureDef.restitution = 0;

        body.createFixture(fixtureDef);

        boxShape.dispose();

        return body;
    }

    public static Fixture addHurtboxToBody(World world, Box2dComponent b2dCmp, float width, float height, float xOffset,
                                           float yOffset) {
        // Create the shape for the sensor (e.g., rectangle)
        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(width / 2, height / 2, new Vector2(xOffset, yOffset), 0);

        // Create the fixture definition for the sensor
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true; // Set the fixture as a sensor
        fixtureDef.filter.categoryBits = FilteredContactListener.HURTBOX_CATEGORY;// hurtbox
        fixtureDef.filter.maskBits = FilteredContactListener.HITBOX_CATEGORY;// hitbox

        // Attach the fixture to the existing body
        b2dCmp.hurtbox = b2dCmp.body.createFixture(fixtureDef);

        // Dispose of the shape after creating the fixture
        sensorShape.dispose();
        return b2dCmp.hurtbox;
    }

    public static Body createBodyWithHitbox(AttackComponent hitCmp, World world, float width, float height,
                                            float xOffset, float yOffset, Vector2 pos) {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = pos.x + xOffset;
        bodyDef.position.y = pos.y + yOffset;
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        // Create the shape for the sensor (e.g., rectangle)
        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(width / 2, height / 2, new Vector2(xOffset, yOffset), 0);

        // Create the fixture definition for the sensor
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true; // Set the fixture as a sensor
        fixtureDef.filter.categoryBits = FilteredContactListener.HITBOX_CATEGORY;
        fixtureDef.filter.maskBits = FilteredContactListener.HURTBOX_CATEGORY;

        hitCmp.body = body;
        hitCmp.hitbox = hitCmp.body.createFixture(fixtureDef);
        hitCmp.hitbox.setUserData(hitCmp);
        return body;

    }

    public static Fixture createSensor(Body body, Shape shape, short fltrCategory, short fltrMask) {
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = fltrCategory;
        fixtureDef.filter.maskBits = fltrMask;

        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();

        return fixture;
    }

    public static Fixture createRadialSensor(Body body, float radius, short fltrCategory, short fltrMask) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        circleShape.setPosition(body.getLocalCenter());

        return createSensor(body, circleShape, fltrCategory, fltrMask);
    }

    public static Fixture createPolygonSensor(Body body, Polygon polygon, short fltrCategory, short fltrMask) {
        PolygonShape polygonShape = new PolygonShape();

        float[] scaledVertices = new float[polygon.getVertices().length];
        for (int i = 0; i < polygon.getVertices().length; i++) {
            scaledVertices[i] = polygon.getVertices()[i] * Strafer.SCALE_FACTOR;
        }

        polygonShape.set(scaledVertices);

        return createSensor(body, polygonShape, fltrCategory, fltrMask);
    }

    public static Fixture createRectangleSensor(Body body, Rectangle rectangle, short fltrCategory, short fltrMask) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width * Strafer.SCALE_FACTOR / 2f, rectangle.height * Strafer.SCALE_FACTOR / 2f);

        return createSensor(body, polygonShape, fltrCategory, fltrMask);
    }

    private static BodyDef getBodyDef(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        return bodyDef;
    }


    private Box2DFactory() {
    }

}