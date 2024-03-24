package com.strafergame.game.world.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.strafergame.Strafer;

public abstract class Box2DMapFactory {


    public static Body createCollisionBody(World world, MapObject mapObject) {
        float sf = Strafer.SCALE_FACTOR;
        Body body = null;
        if (mapObject instanceof RectangleMapObject) {
            RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
            Rectangle rectangle = rectangleObject.getRectangle();

            BodyDef bodyDef = getBodyDef(rectangle.getX() * sf + rectangle.getWidth() * sf / 2f, rectangle.getY() * sf + rectangle.getHeight() * sf / 2f);
            bodyDef.fixedRotation = true;
            bodyDef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(rectangle.getWidth() * sf / 2f, rectangle.getHeight() * sf / 2f);
            body.createFixture(polygonShape, 0.0f);
            polygonShape.dispose();

        } else if (mapObject instanceof EllipseMapObject) {
            EllipseMapObject circleMapObject = (EllipseMapObject) mapObject;
            Ellipse ellipse = circleMapObject.getEllipse();

            BodyDef bodyDef = getBodyDef(ellipse.x * sf, ellipse.y * sf);

            if (ellipse.width != ellipse.height) throw new IllegalArgumentException("Only circles are allowed.");

            body = world.createBody(bodyDef);
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
            bodyDef.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(scaledVertices);

            body.createFixture(polygonShape, 0.0f);

            polygonShape.dispose();
        }
        return body;
    }

    public static Body createSensorBody(World world, MapObject mapObject, short fltrCategory, short fltrMask) {
        Body body = null;

        float sf = Strafer.SCALE_FACTOR;

        BodyDef bodyDef = new BodyDef();
        bodyDef.fixedRotation = true;

        bodyDef.type = BodyDef.BodyType.StaticBody;

        if (mapObject instanceof RectangleMapObject) {
            RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
            Rectangle rectangle = rectangleObject.getRectangle();

            bodyDef.position.set(rectangle.getX() * sf + rectangle.getWidth() * sf / 2f, rectangle.getY() * sf + rectangle.getHeight() * sf / 2f);
            body = world.createBody(bodyDef);
            Box2DFactory.createRectangleSensor(body, rectangle, fltrCategory, fltrMask);

        } else if (mapObject instanceof PolygonMapObject) {
            PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
            Polygon polygon = polygonMapObject.getPolygon();

            bodyDef.position.set(polygon.getX() * sf, polygon.getY() * sf);
            body = world.createBody(bodyDef);
            Box2DFactory.createPolygonSensor(body, polygon, fltrCategory, fltrMask);
        }
        return body;
    }

    private static BodyDef getBodyDef(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        return bodyDef;
    }

}
