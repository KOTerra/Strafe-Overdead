package com.strafergame.game.entities.player;

import com.strafergame.Strafer;
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.EntityType;
import com.strafergame.input.PlayerControl;

public class Player extends Entity {

	public Player() {
		super(EntityType.PLAYER);
	}

	@Override
	protected void move() {
		super.move();
		dirX = 0;
		dirY = 0;

		if (Strafer.worldCamera.getFocusEntity().equals(this)) {
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
		}

	}
}
