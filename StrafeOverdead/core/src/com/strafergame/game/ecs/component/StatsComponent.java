package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * constants for entity stats
 */
public class StatsComponent implements Component {
    public float baseSpeed = 12f;
    public float dashForce = 20f;
    public float dashDuration = 1f;
    public float dashCooldownDuration = .25f;
}
