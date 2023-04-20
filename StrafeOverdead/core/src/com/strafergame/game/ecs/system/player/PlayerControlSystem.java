package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.entities.EntityDirection;
import com.strafergame.input.PlayerControl;

public class PlayerControlSystem extends IteratingSystem {

	public PlayerControlSystem() {
		super(Family.all(PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		move(entity);
	}

	private void move(Entity e) {
		PositionComponent posCmp = ComponentMappers.position().get(e);
		MovementComponent movCmp = ComponentMappers.movement().get(e);

		movCmp.dirX = 0;
		movCmp.dirY = 0;

		if (PlayerControl.MOVE_UP) {
			movCmp.dirY = 1;
			posCmp.direction = EntityDirection.w;
		}
		if (PlayerControl.MOVE_DOWN) {
			movCmp.dirY = -1;
			posCmp.direction = EntityDirection.s;
		}
		if (PlayerControl.MOVE_LEFT) {
			movCmp.dirX = -1;
			posCmp.direction = EntityDirection.a;
		}
		if (PlayerControl.MOVE_RIGHT) {
			movCmp.dirX = 1;
			posCmp.direction = EntityDirection.d;
		}

	}

}
