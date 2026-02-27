package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * constants for entity stats
 * defaults are for player
 */
public class StatsComponent implements Component {
    public float maxHealth = 200;
    public float baseSpeed = 12;

    public float dashForce = 40f;
    public float dashDuration = .4f;
    public float dashCooldownDuration = .25f;

    public float meleeAttackDuration = .8f; //16frames*0.05
    public float meleeAttackDamagePerSecond = 100;
    public float meleeKnockbackMagnitude = 1000;

    public float rangedAttackDuration = .3f;
    public float rangedAttackInstantDamage = 10;
    public float rangedAttackDeletionTime = 2.f;
}
