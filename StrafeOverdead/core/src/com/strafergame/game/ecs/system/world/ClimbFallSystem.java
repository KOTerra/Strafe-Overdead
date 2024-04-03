package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;

public class ClimbFallSystem extends IteratingSystem {
    public ClimbFallSystem() {
        super(Family.all(ElevationComponent.class, Box2dComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //verify footprint stack for elevation
        //raycast map layers down to check if falling then state=FALL,fall a number of tiles that match perspective offset thenchange elevation
    }
}
