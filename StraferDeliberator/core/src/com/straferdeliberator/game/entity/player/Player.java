package com.straferdeliberator.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.Entity;
import com.straferdeliberator.game.entity.EntityType;
import com.straferdeliberator.input.handlers.controller.ControllerListenerBase;

public class Player extends Entity {

	ControllerListenerBase cm = new ControllerListenerBase();

	public Player() {
		super(EntityType.PLAYER);
	}

	@Override
	protected void move(float delta) {
		dirX = 0;
		dirY = 0;

		if (cm.buttonDown(Controllers.getCurrent(), Controllers.getCurrent().getMapping().buttonDpadRight)) {
			dirX = 1;
			direction = 'd';
		}

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
