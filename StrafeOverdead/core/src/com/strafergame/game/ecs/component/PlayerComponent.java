package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {

	public float baseSpeed = 10f;
	public float dashForce = 25f;
	public float dashCooldown = 3f;
}
