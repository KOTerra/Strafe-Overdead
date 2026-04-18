package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.ActivatorType;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.collision.ElevationUtils;

import static com.strafergame.game.ecs.system.world.ClimbFallSystem.TARGET_NOT_CALCULATED;

public class ClimbDelegate {

    public void climb(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (b2dCmp.footprintStack.size() >= 2) {
            Entity first = b2dCmp.footprintStack.pop();
            Entity second = b2dCmp.footprintStack.getFirst();
            ActivatorComponent actvA = ComponentMappers.activator().get(first);
            ActivatorComponent actvB = ComponentMappers.activator().get(second);
            ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(actvA.agent);

            if (actvA.agent.equals(actvB.agent)) {                                              //activators of the same agent
                if (actvA.type.equals(ActivatorType.ELEVATION_UP) && actvB.type.equals(ActivatorType.ELEVATION_DOWN)) { //goes down
                    ElevationUtils.changeElevation(entity, agentCmp.baseElevation);
                    b2dCmp.footprintStack.clear();      //solved clear
                    b2dCmp.footprintStack.addFirst(first);
                    return;
                }
                if (actvA.type.equals(ActivatorType.ELEVATION_DOWN) && actvB.type.equals(ActivatorType.ELEVATION_UP)) { //goes up
                    ElevationUtils.changeElevation(entity, agentCmp.topElevation);
                    b2dCmp.footprintStack.clear();
                    b2dCmp.footprintStack.addFirst(first);
                    return;
                }
            }
            b2dCmp.footprintStack.addFirst(first);//put back
        }
    }

    public void lowElevationClamping(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);

        if (typeCmp != null && typeCmp.entityState.equals(EntityState.fall)) {
            return;
        }

        //  we are moving  check if the tile ahead at the current elevation is empty.
        // if empty we are  walking off a ledge so no clamp up
        Vector2 velocity = b2dCmp.body.getLinearVelocity();
        if (velocity.len2() > 0.1f) {
            float lookAheadDist = velocity.y > 0 ? 0.7f : 0.5f;
            float predictX = b2dCmp.body.getPosition().x + (velocity.x != 0 ? (velocity.x > 0 ? lookAheadDist : -lookAheadDist) : 0);
            float predictY = b2dCmp.body.getPosition().y + (velocity.y != 0 ? (velocity.y > 0 ? lookAheadDist : -lookAheadDist) : 0);

            if (!MapQueryUtils.isTileAt(Math.round(predictX), Math.round(predictY), elvCmp.elevation)) {
                return;
            }
        }

        // If the gap check passed (or we are standing still), check if we are physically under a higher tile.
        int checkElevation = elvCmp.elevation + 1;
        int xRound = Math.round(b2dCmp.body.getPosition().x);
        int yRound = Math.round(b2dCmp.body.getPosition().y);

        if (MapQueryUtils.isTileAt(xRound, yRound, checkElevation)) {
            ElevationUtils.changeElevation(entity, checkElevation);

            // Safety: Clear fall targets to prevent stale state from a previous frame
            elvCmp.fallTargetCell = null;
            elvCmp.fallTargetY = TARGET_NOT_CALCULATED;
            elvCmp.fallTargetElevation = -1;
        }
    }
}
