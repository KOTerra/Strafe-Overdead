package com.straferdeliberator.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;
import com.straferdeliberator.game.entity.EntityType;
import com.straferdeliberator.input.PlayerControl;

public class Player extends Entity {

	public Player() {
		super(EntityType.PLAYER);
	}

	@Override
	protected void move(float delta) {
		dirX = 0;
		dirY = 0;

		if (PlayerControl.MOVE_UP) {
			dirY = 1;
			direction = 'w';
		}
		if (PlayerControl.MOVE_DOWN) {
			dirY = -1;
			direction = 's';
		}
		if (PlayerControl.MOVE_LEFT) {
			dirX = -1;
			direction = 'a';
		}
		if (PlayerControl.MOVE_RIGHT) {
			dirX = 1;
			direction = 'd';
		}

		body.setLinearVelocity(dirX * speed, dirY * speed);
		this.setPosition(body.getPosition().x, body.getPosition().y);

		if (Strafer.inDebug) {
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_SUBTRACT)) {
				Strafer.worldCamera.zoom += .02f;
			}
			if (Gdx.input.isKeyPressed(Keys.NUMPAD_ADD)) {
				Strafer.worldCamera.zoom -= .02f;
			}
		}
	}
}
