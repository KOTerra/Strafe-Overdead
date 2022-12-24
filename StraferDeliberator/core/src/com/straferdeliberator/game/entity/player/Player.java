package com.straferdeliberator.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;
import com.straferdeliberator.game.entity.EntityType;

public class Player extends Entity {

	Vector3 cameraPosition = new Vector3();

	public Player() {
		entityType = EntityType.PLAYER;

		speed = 2.5f;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		move(delta);
		updateCamera();

	}

	private void move(float delta) {
		dirX = 0;
		dirY = 0;

		if (Gdx.input.isKeyPressed(Keys.W)) {
			dirY = 1;
			direction = 'w';
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			dirY = -1;
			direction = 's';
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			dirX = -1;
			direction = 'a';
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			dirX = 1;
			direction = 'd';
		}

		body.setLinearVelocity(dirX * speed, dirY * speed);
		this.setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

		if (Strafer.inDebug) {
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
				Strafer.worldCamera.zoom += .02f;
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
				Strafer.worldCamera.zoom -= .02f;
			}
		}
	}

	private void updateCamera() {
		cameraPosition.x = body.getPosition().x;
		cameraPosition.y = body.getPosition().y;
		Strafer.worldCamera.position.lerp(cameraPosition, .05f);

	}

}
