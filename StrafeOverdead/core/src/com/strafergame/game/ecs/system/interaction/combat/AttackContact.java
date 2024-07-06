package com.strafergame.game.ecs.system.interaction.combat;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;

public class AttackContact {
    public Fixture hitbox;
    public Fixture hurtbox;

    public AttackContact(Fixture hurtbox, Fixture hitbox) {

        this.hitbox = hitbox;
        this.hurtbox = hurtbox;
    }

    public AttackComponent getAttack() {
        return (AttackComponent) hitbox.getUserData();
    }

    public static AttackComponent getAttack(Box2dComponent b2dCmp) {
        AttackContact contact = (AttackContact) b2dCmp.hurtbox.getUserData();
        if (contact != null) {
            return (AttackComponent) contact.hitbox.getUserData();
        }
        return null;
    }
}
