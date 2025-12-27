package com.strafergame.game.ecs.system.ai;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ai.BehaviorTreeComponent;

public class NpcSystem extends IteratingSystem {

    public NpcSystem() {
        super(Family.all(BehaviorTreeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BehaviorTreeComponent btc = ComponentMappers.behaviorTree().get(entity);

        if (btc != null && btc.tree != null) {
            if (btc.tree.getObject() == null) {//create bind
                btc.tree.setObject(entity);
            }

            btc.tree.step();
        }
    }
}