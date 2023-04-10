package com.strafergame.game.entities.player;

import com.strafergame.Strafer;
import com.strafergame.game.entities.Entity;
import com.strafergame.game.entities.EntityDirection;
import com.strafergame.game.entities.EntityType;
import com.strafergame.input.PlayerControl;

public class Player extends Entity {

	public Player() {
		super(EntityType.player);
	}

	@Override
	protected void move() {
		super.move();
		dirX = 0;
		dirY = 0;

		if (Strafer.worldCamera.getFocusEntity().equals(this)) {
			if (PlayerControl.MOVE_UP) {
				dirY = 1;
				direction = EntityDirection.w;
			}
			if (PlayerControl.MOVE_DOWN) {
				dirY = -1;
				direction = EntityDirection.s;
			}
			if (PlayerControl.MOVE_LEFT) {
				dirX = -1;
				direction = EntityDirection.a;
			}
			if (PlayerControl.MOVE_RIGHT) {
				dirX = 1;
				direction = EntityDirection.d;
			}
		}

	}
}
