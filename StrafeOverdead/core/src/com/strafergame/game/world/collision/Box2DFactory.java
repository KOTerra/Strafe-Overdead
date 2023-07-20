package com.strafergame.game.world.collision;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Box2DFactory {

	public static Body createBody(Box2dComponent b2dCmp, World world, float width, float height, float xOffset,
			float yOffset, Vector3 pos, BodyDef.BodyType type) {
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

	public static Fixture addSensorToBody(World world, Box2dComponent b2dCmp, float width, float height, float xOffset,
			float yOffset) {
		// Create the shape for the sensor (e.g., rectangle)
		PolygonShape sensorShape = new PolygonShape();
		sensorShape.setAsBox(width / 2, height / 2, new Vector2(xOffset, yOffset), 0);

		// Create the fixture definition for the sensor
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = sensorShape;
		fixtureDef.isSensor = true; // Set the fixture as a sensor

		// Attach the fixture to the existing body
		Fixture fixture = b2dCmp.body.createFixture(fixtureDef);
		b2dCmp.hitbox = fixture;
		// Dispose of the shape after creating the fixture
		sensorShape.dispose();
		return fixture;
	}

	public static Body createSensor(World world, float width, float height, float xOffset, float yOffset, Vector3 pos,
			BodyDef.BodyType type) {
		Body body;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.x = pos.x + xOffset;
		bodyDef.position.y = pos.y + yOffset;
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		bodyDef.type = type;
		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(width / 2, height / 2);

		fixtureDef.shape = boxShape;
		fixtureDef.isSensor = true;

		body.createFixture(fixtureDef);
		boxShape.dispose();

		return body;
	}

	public static void createWall(World world, float width, float height, Vector3 pos) {
		Body body;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set((pos.x + width / 2), (pos.y + height / 2));
		bodyDef.angle = 0;
		bodyDef.fixedRotation = true;
		bodyDef.type = BodyType.StaticBody;
		body = world.createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(width / 2, height / 2);

		fixtureDef.shape = boxShape;
		fixtureDef.restitution = 0;

		body.createFixture(fixtureDef);
		boxShape.dispose();
	}

	private Box2DFactory() {
	}

}