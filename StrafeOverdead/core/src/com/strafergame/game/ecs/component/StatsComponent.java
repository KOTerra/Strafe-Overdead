package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * constants for entity stats
 */
public class StatsComponent implements Component {
    public float baseSpeed = 12f;
    public float dashForce = 10f;
    public float dashDuration = .5f;
    public float dashCooldownDuration = .25f;
    public float meleeAttackDuration = .8f; //16frames*0.05
    public float meleeAttackDamagePerSecond = 10;
}
