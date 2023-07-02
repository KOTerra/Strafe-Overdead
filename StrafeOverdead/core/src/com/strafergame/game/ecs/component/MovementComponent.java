package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.strafergame.game.entities.EntityDirection;

public class MovementComponent implements Component {
	public EntityDirection direction = EntityDirection.s;
	public float dirX = 0f;
	public float dirY = 0f;
	public float speed = 0f;
	public float dashForce = 0f;
}
