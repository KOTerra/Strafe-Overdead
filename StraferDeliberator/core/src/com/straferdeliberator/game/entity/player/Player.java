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

	private float speed = 2.5f;
	Vector3 cameraPosition = new Vector3();

	public Player() {

		animation = new Animation<TextureRegion>(0.25f,
				Strafer.assetManager.get("spritesheets/player/player-idle.atlas", TextureAtlas.class).findRegions("s/idle"),
				PlayMode.LOOP);

		setPosition(Strafer.WORLD_WIDTH / 2 - getWidth() / 2, Strafer.WORLD_HEIGHT / 2 - getHeight() / 2);

	}

	@Override
	public void act(float delta) {
		super.act(delta);
		move(delta);
		updateCamera();
	}

	private void move(float delta) {
		float translate = speed * delta;
		if (Gdx.input.isKeyPressed(Keys.D)) {
			this.setPosition(this.getX() + translate, this.getY());
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			this.setPosition(this.getX() - translate, this.getY());
		}
		if (Gdx.input.isKeyPressed(Keys.W)) {
			this.setPosition(this.getX(), this.getY() + translate);
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			this.setPosition(this.getX(), this.getY() - translate);
		}

		if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
			Strafer.worldCamera.zoom += .02f;
		}
		if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
			Strafer.worldCamera.zoom -= .02f;
		}
	}

	private void updateCamera() {
		cameraPosition.x = this.getX() + 1;
		cameraPosition.y = this.getY() + 1;
		Strafer.worldCamera.position.lerp(cameraPosition, .05f);

	}

}
