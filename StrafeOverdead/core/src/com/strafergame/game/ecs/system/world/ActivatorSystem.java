package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.component.world.ActivatorComponent;

public class ActivatorSystem extends IteratingSystem {
    public ActivatorSystem() {
        super(Family.all(ActivatorComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
