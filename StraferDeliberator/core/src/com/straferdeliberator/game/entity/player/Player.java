package com.straferdeliberator.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;

public class Player extends Entity {

	private float speed = 2.5f;
	Vector3 cameraPosition = new Vector3();

	public Player() {
		sprite = new Sprite(Strafer.assetManager.get("assets/pep.png", Texture.class));
		sprite.setSize(sprite.getWidth() * Strafer.SCALE_FACTOR, sprite.getHeight() * Strafer.SCALE_FACTOR);
		sprite.setPosition(Strafer.WORLD_WIDTH / 2 - sprite.getWidth() / 2,
				Strafer.WORLD_HEIGHT / 2 - sprite.getHeight() / 2);

	}

	@Override
	public void act(float delta) {
		move(delta);
		updateCamera();
	}

	private void move(float delta) {
		if (Gdx.input.isKeyPressed(Keys.D)) {
			sprite.setPosition(sprite.getX() + speed * delta, sprite.getY());
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			sprite.setPosition(sprite.getX() - speed * delta, sprite.getY());
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			sprite.setPosition(sprite.getX(), sprite.getY() + speed * delta);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			sprite.setPosition(sprite.getX(), sprite.getY() - speed * delta);
		}

		if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
			Strafer.worldCamera.zoom += .02f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
			Strafer.worldCamera.zoom -= .02f;
		}
	}

	private void updateCamera() {
		cameraPosition.x = sprite.getX() + 1;
		cameraPosition.y = sprite.getY() + 1;
		Strafer.worldCamera.position.lerp(cameraPosition, .05f);

	}

}
