package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.component.ElevationAgentComponent;
import com.strafergame.game.world.collision.Box2DWorld;

public class ElevationSystem extends IteratingSystem {


    Box2DWorld box2dWorld;

    public ElevationSystem(Box2DWorld box2dWorld) {
        super(Family.all(ElevationAgentComponent.class).get());
        this.box2dWorld = box2dWorld;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //daca slope ia entity u de pe el si ii da snap pe laterala si il muta dupa directie daca e aceeasi urca sau opusa il coboara cu compus de vectori ceva
    }
}
