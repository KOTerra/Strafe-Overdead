package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.entities.EntityDirection;
import com.strafergame.game.entities.EntityState;
import com.strafergame.input.PlayerControl;

public class PlayerControlSystem extends IteratingSystem {

	public PlayerControlSystem() {
		super(Family.all(PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		move(entity);
		dash(entity);
	}

	private void move(Entity e) {
		PositionComponent posCmp = ComponentMappers.position().get(e);
		final MovementComponent movCmp = ComponentMappers.movement().get(e);
		PlayerComponent plyrCmp = ComponentMappers.player().get(e);
		Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
		final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

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
		if (movCmp.moving()) {
			typeCmp.entityState = EntityState.walk;
		} else {
			typeCmp.entityState = EntityState.idle;
		}

	}

	private void dash(Entity e) {
		final MovementComponent movCmp = ComponentMappers.movement().get(e);
		final EntityTypeComponent ettCmp = ComponentMappers.entityType().get(e);
		final PlayerComponent plyrCmp = ComponentMappers.player().get(e);

		if (!movCmp.isDashCooldown) {
			if (PlayerControl.DASH && movCmp.moving()) {
				movCmp.isDashCooldown = true;
				ettCmp.entityState = EntityState.dash;
				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						ettCmp.entityState = EntityState.idle;
						Timer.schedule(new Timer.Task() {
							@Override
							public void run() {
								movCmp.isDashCooldown = false;
							}
						}, plyrCmp.dashCooldown);
					}
				}, movCmp.dashDuration);
				

			}
		}
	}

}
