package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.ActivatorType;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.map.MapManager;

public class ClimbFallSystem extends IteratingSystem {
    public ClimbFallSystem() {
        super(Family.all(ElevationComponent.class, Box2dComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        climb(entity);

        computeJumpHeight(entity);
        jumpArrive(entity);

        if (shouldFall(entity) && !isClimbing(entity)) {
            ComponentMappers.entityType().get(entity).entityState = EntityState.fall;
            computeFallTarget(entity);
        }
        fallArrive(entity);
    }


    public static boolean isClimbing(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        return !b2dCmp.footprintStack.isEmpty();
    }

    /**
     * takes the entities stack of previous activators and decides if and how to elevate it upwards or downwards(climbing or descending)
     */
    private void climb(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);

        if (b2dCmp.footprintStack.size() >= 2) {
            Entity first = b2dCmp.footprintStack.pop();
            Entity second = b2dCmp.footprintStack.getFirst();
            ActivatorComponent actvA = ComponentMappers.activator().get(first);
            ActivatorComponent actvB = ComponentMappers.activator().get(second);
            ElevationAgentComponent agentCmp = ComponentMappers.elevationAgent().get(actvA.agent);

            if (actvA.agent.equals(actvB.agent)) {                                              //activators of the same agent
                if (actvA.type.equals(ActivatorType.ELEVATION_UP) && actvB.type.equals(ActivatorType.ELEVATION_DOWN)) { //goes down
                    elvCmp.elevation = agentCmp.baseElevation;
                    b2dCmp.footprintStack.clear();      //solved clear
                    return;
                }
                if (actvA.type.equals(ActivatorType.ELEVATION_DOWN) && actvB.type.equals(ActivatorType.ELEVATION_UP)) { //goes up
                    elvCmp.elevation = agentCmp.topElevation;
                    b2dCmp.footprintStack.clear();
                    return;
                }
            }
            b2dCmp.footprintStack.addFirst(first);//put back
        }

    }

    private void computeJumpHeight(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (typeCmp.entityState == EntityState.jump && !elvCmp.jumpTaken) {
            elvCmp.jumpTaken = true;
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            SpriteComponent spriteCmp = ComponentMappers.sprite().get(entity);
            elvCmp.jumpHeight = spriteCmp.height * 1.5f + b2dCmp.body.getPosition().y;
        }
    }

    private void jumpArrive(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp.entityState == EntityState.jump) {

            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
            System.out.println(b2dCmp.body.getPosition().y + " " + elvCmp.jumpHeight);
            if (b2dCmp.body.getPosition().y > elvCmp.jumpHeight) {
                elvCmp.elevation = elvCmp.elevation + 1;
                ComponentMappers.position().get(entity).elevation = elvCmp.elevation;
                typeCmp.entityState = EntityState.fall;
                elvCmp.jumpHeight = 0f;
                elvCmp.jumpTaken = false;
            }
        }
    }

    /**
     * checks if a tile layer is right underneath the entity's footprint on the same elevation i.e. touching ground
     */
    public static boolean shouldFall(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        if (MapManager.getLayersElevatedMap(elvCmp.elevation) == null) {
            for (MapLayer layer : MapManager.getLayersElevatedMap(elvCmp.elevation)) {
                if (layer instanceof TiledMapTileLayer tileLayer && tileLayer.getCell((int) b2dCmp.body.getPosition().x, (int) b2dCmp.body.getPosition().y) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * raycasts through the tile layers below the entity finding the first non-null tile, taking perspective offset on Y axis in consideration
     */
    private void computeFallTarget(Entity entity) {         //solve fall after jumping in null layer, maybe add an empty layer on top of the map, handled automatically
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (elvCmp.fallTargetCell == null) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            for (int elevation = elvCmp.elevation; elevation >= 0; elevation--) {
                if (MapManager.getLayersElevatedMap(elevation) != null) {
                    for (MapLayer layer : MapManager.getLayersElevatedMap(elevation)) {
                        if (layer instanceof TiledMapTileLayer tileLayer) {
                            int targetY = (int) (b2dCmp.body.getPosition().y - (elvCmp.elevation - elevation));
                            TiledMapTileLayer.Cell cell = tileLayer.getCell((int) b2dCmp.body.getPosition().x, targetY);
                            if (cell != null) {
                                elvCmp.fallTargetCell = cell;
                                elvCmp.fallTargetY = targetY;
                                elvCmp.fallTargetElevation = elevation;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * stops the entity from falling if it hits the fallTargets
     */
    private void fallArrive(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp.entityState.equals(EntityState.fall)) {
            ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
            if (elvCmp.fallTargetCell != null) {
                //fall to it
                // upon arrival state=idle or return to before falling if falling through map
                //place shadow on the target height
                Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
                if (b2dCmp.body.getPosition().y <= elvCmp.fallTargetY) {
                    typeCmp.entityState = EntityState.idle;
                    elvCmp.elevation = elvCmp.fallTargetElevation;
                    ComponentMappers.position().get(entity).elevation = elvCmp.fallTargetElevation;
                    elvCmp.fallTargetCell = null;
                }
            }
        }
    }
}
