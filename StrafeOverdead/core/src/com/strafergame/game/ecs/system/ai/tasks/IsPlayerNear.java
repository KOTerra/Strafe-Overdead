package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.system.interaction.ProximityContact;
import com.strafergame.game.world.GameWorld;

public class IsPlayerNear extends LeafTask<Entity> {

    @Override
    public Status execute() {
        Entity e = getObject();

        if (GameWorld.player == null) {
            return Status.FAILED;
        }
        DetectorComponent dtctrCmp = ComponentMappers.detector().get(e);

        if (dtctrCmp != null && ProximityContact.isPlayerInProximity(dtctrCmp)) {
            SteeringComponent steerCmp = ComponentMappers.steering().get(e);
            if (steerCmp != null) {
                steerCmp.target = GameWorld.player;
            }
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return (task != null) ? (IsPlayerNear) task : new IsPlayerNear();
    }
}