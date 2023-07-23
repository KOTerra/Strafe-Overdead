package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

public class PlayerComponent implements Component {

	public float baseSpeed = 12f;
	public float dashForce = 20f;
	public float dashCooldownDuration = .25f;
	public Fixture sensor;
	public Array<Fixture> nearDetectors = new Array<>();
}
