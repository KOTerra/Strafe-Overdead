package com.strafergame.game.world.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.Box2dComponent;

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

    public static Fixture createSensor(Body body, float radius, short fltrCategory, short fltrMask) {
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        circle.setPosition(body.getLocalCenter());

        fixtureDef.shape = circle;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = fltrCategory;
        fixtureDef.filter.maskBits = fltrMask;
        Fixture fixture = body.createFixture(fixtureDef);
        circle.dispose();

        return fixture;
    }
    public static void createCollision(World world, MapObject mapObject) {
        float sf = Strafer.SCALE_FACTOR;
        if (mapObject instanceof RectangleMapObject) {
            RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
            Rectangle rectangle = rectangleObject.getRectangle();

            BodyDef bodyDef = getBodyDef(rectangle.getX() * sf + rectangle.getWidth() * sf / 2f, rectangle.getY() * sf + rectangle.getHeight() * sf / 2f);
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyType.StaticBody;
            Body body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(rectangle.getWidth() * sf / 2f, rectangle.getHeight() * sf / 2f);
            body.createFixture(polygonShape, 0.0f);
            polygonShape.dispose();

        } else if (mapObject instanceof EllipseMapObject) {
            EllipseMapObject circleMapObject = (EllipseMapObject) mapObject;
            Ellipse ellipse = circleMapObject.getEllipse();

            BodyDef bodyDef = getBodyDef(ellipse.x * sf, ellipse.y * sf);

            if (ellipse.width != ellipse.height)
                throw new IllegalArgumentException("Only circles are allowed.");

            Body body = world.createBody(bodyDef);
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(ellipse.width * sf / 2f);
            body.createFixture(circleShape, 0.0f);
            circleShape.dispose();
        } else if (mapObject instanceof PolygonMapObject) {
            PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
            Polygon polygon = polygonMapObject.getPolygon();

            float[] scaledVertices = new float[polygon.getVertices().length];
            for (int i = 0; i < polygon.getVertices().length; i++) {
                scaledVertices[i] = polygon.getVertices()[i] * sf;
            }

            BodyDef bodyDef = getBodyDef(polygon.getX() * sf, polygon.getY() * sf);
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyType.StaticBody;
            Body body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(scaledVertices);

            body.createFixture(polygonShape, 0.0f);

            polygonShape.dispose();
        }
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