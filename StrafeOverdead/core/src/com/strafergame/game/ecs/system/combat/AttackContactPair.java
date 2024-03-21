package com.strafergame.game.ecs.system.combat;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;

public class AttackContactPair {
    public Fixture hitbox;
    public Fixture hurtbox;

    public AttackContactPair(Fixture hurtbox, Fixture hitbox) {

        this.hitbox = hitbox;
        this.hurtbox = hurtbox;
    }

    public AttackComponent getAttack() {
        return (AttackComponent) hitbox.getUserData();
    }

    public static AttackComponent getAttack(Box2dComponent b2dCmp) {
        AttackContactPair pair = (AttackContactPair) b2dCmp.hurtbox.getUserData();
        if (pair != null) {
            return (AttackComponent) pair.hitbox.getUserData();
        }
        return null;
    }
}
