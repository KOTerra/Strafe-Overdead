package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.ElevationAgentType;
import com.strafergame.game.ecs.states.EntityDirection;
import com.strafergame.game.world.collision.Box2DWorld;

import javax.swing.*;

public class ElevationSystem extends IteratingSystem {


    Box2DWorld box2dWorld;

    public ElevationSystem(Box2DWorld box2dWorld) {
        super(Family.all(ElevationAgentComponent.class).get());
        this.box2dWorld = box2dWorld;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        //daca slope ia entity u de pe el si ii da snap pe laterala si il muta dupa directie daca e aceeasi urca sau opusa il coboara cu compus de vectori ceva
        ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(entity);

        for (Entity e : agentCmp.interactingEntitites) {

            PositionComponent posCmp = ComponentMappers.position().get(e);
            EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
            MovementComponent movCmp = ComponentMappers.movement().get(e);
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);

            if (agentCmp.type.equals(ElevationAgentType.SLOPE)) {


                if (movCmp.isMoving()) {
                    if (posCmp.direction.equals(EntityDirection.d)) {
                        if (agentCmp.direction.equals(EntityDirection.d)) {//        >^/
                            movCmp.dir.add(0, 1);
                        }
                        if (agentCmp.direction.equals(EntityDirection.a)) {//       <v/
                            movCmp.dir.add(0, -1);
                        }
                    }
                    if (posCmp.direction.equals(EntityDirection.a)) {
                        if (agentCmp.direction.equals(EntityDirection.a)) {//       >^\
                            movCmp.dir.add(0, 1);
                        }
                        if (agentCmp.direction.equals(EntityDirection.d)) {//       <v\
                            movCmp.dir.add(0, -1);
                        }
                    }
                    movCmp.dir.scl(.7f);
                }
            }


        }
    }
}
