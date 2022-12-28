package com.straferdeliberator.game.world.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class Box2DHelper {

	public static Body createBody(World world, float width, float height, float xOffset, float yOffset, Vector3 pos,
			BodyDef.BodyType type) {
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

		body.createFixture(fixtureDef);
		boxShape.dispose();

		return body;
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

	private Box2DHelper() {
	}

}