package com.straferdeliberator.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;

public class Player extends Entity {

	Vector3 cameraPosition = new Vector3();

	public Player() {
		speed = 2.5f;
		animation = new Animation<TextureRegion>(0.35f, Strafer.assetManager
				.get("spritesheets/player/player-idle.atlas", TextureAtlas.class).findRegions("s/idle"), PlayMode.LOOP);

		setPosition(Strafer.WORLD_WIDTH / 2, Strafer.WORLD_HEIGHT / 2);

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
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			dirX = -1;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			dirY = -1;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			dirX = 1;
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
