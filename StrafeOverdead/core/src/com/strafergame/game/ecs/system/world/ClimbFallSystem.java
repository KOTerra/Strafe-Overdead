package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.world.ActivatorComponent;
import com.strafergame.game.ecs.component.world.ElevationAgentComponent;
import com.strafergame.game.ecs.states.ActivatorType;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.map.MapManager;
import org.antlr.v4.runtime.misc.Pair;

import java.util.List;


public class ClimbFallSystem extends IteratingSystem {

    private final int OFF_WORLD_FALL_DISTANCE = -10;
    public static final int TARGET_NOT_CALCULATED = -100;

    public ClimbFallSystem() {
        super(Family.all(ElevationComponent.class, Box2dComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        climb(entity);
        if (canJump(entity) && !isClimbing(entity)) {
            beginJump(entity);                   //maybe disable jump if climbing to prohibit jumping on slopes
        }
        jumpArrive(entity);

        if (shouldFall(entity) && !isClimbing(entity)) {    //change is climbing to not be true if just passed activator but not went up on elevation agent
            ComponentMappers.entityType().get(entity).entityState = EntityState.fall;
            computeFallTarget(entity);
        }
        updateFallTarget(entity);

        fallArrive(entity);
        fallOffWorld(entity);
    }


    public static boolean isClimbing(Entity entity) {
        return ComponentMappers.elevation().get(entity).isClimbing;
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

    private boolean canJump(Entity entity) {
        //raycast above if one elevation is above don t
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity); //mYBE CHnange elevation at the start of the jump maybe intermediate state tryjump to check if canjump then change elevation and then move upwards
        if (!typeCmp.entityState.equals(EntityState.jump)) {
            return false;
        }
        if (!elvCmp.jumpFinished) {
            return false;
        }
        MapLayers layers = MapManager.getLayersElevatedMap(elvCmp.elevation + 2);
        if (layers != null && layers.size() != 0) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer && tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y + 1.5f)) != null) {
                    elvCmp.jumpHeight = 0f;
                    typeCmp.entityState = EntityState.idle;
                    return false;
                }
            }
        }
        return true;
    }


    private void beginJump(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (typeCmp.entityState.equals(EntityState.jump) && !elvCmp.jumpTaken) {
            elvCmp.jumpTaken = true;
            elvCmp.jumpFinished = false;
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

            elvCmp.prevIncrementalY = b2dCmp.body.getPosition().y;
            elvCmp.fallTargetY = b2dCmp.body.getPosition().y;
            elvCmp.fallTargetElevation = elvCmp.elevation;
            elvCmp.fallTargetCell = getFirstCellUnderEntity(entity);
            elvCmp.jumpHeight = b2dCmp.body.getPosition().y + 9.5f;
            elvCmp.jumpElevationDifference = (int) Math.ceil(9.5f);
        }
    }

    private void jumpArrive(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp.entityState.equals(EntityState.jump)) {

            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
            if (b2dCmp.body.getPosition().y >= elvCmp.jumpHeight) {
                elvCmp.elevation = elvCmp.elevation + elvCmp.jumpElevationDifference;
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
    public boolean shouldFall(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        MapLayers layers = MapManager.getLayersElevatedMap(elvCmp.elevation);
        if (elvCmp.jumpTaken) {
            return false;
        }
        if (!elvCmp.jumpFinished) {
            return true;
        }

        if (layers != null && layers.size() != 0) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {
                    if (
                        //tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                        //      tileLayer.getCell((int) (b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                        //    tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null ||
                            tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null
                    ) {
                        saveStablePosition(entity);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //TODO check before and while falling if falltarget is the same as it was in the last iteration (maybe something comes under the player while jumping so we need to see if the target is conserved)

    /**
     * raycasts through the tile layers below the entity finding the first non-null tile, taking perspective offset on Y axis in consideration
     */
    private void computeFallTarget(Entity entity) {         //solve fall after jumping in null layer, maybe add an empty layer on top of the map, handled automatically

        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Pair<TiledMapTileLayer.Cell, Integer[]> raycastPair = raycastFirstCellDown(entity);
        TiledMapTileLayer.Cell cell = raycastPair.a;
        int elevation = raycastPair.b[2];

        if (cell == null || elevation == elvCmp.elevation) {
            return;
        }
        //
        cell.setRotation(TiledMapTileLayer.Cell.ROTATE_90);
        //

        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (elvCmp.fallTargetCell == null && elvCmp.fallTargetY == TARGET_NOT_CALCULATED) {

            float targetY = b2dCmp.body.getPosition().y - (elvCmp.elevation - elevation);

            elvCmp.elevation -= 1;
            ComponentMappers.position().get(entity).elevation -= 1; ///if falling in w direction starts with an elevation down already
            elvCmp.prevIncrementalY = b2dCmp.body.getPosition().y;
            elvCmp.fallTargetCell = cell;
            elvCmp.fallTargetY = targetY;
            elvCmp.fallTargetElevation = elevation;
        }

    }

    private void updateFallTarget(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);


        if (elvCmp.fallTargetCell != null) {
            var v = raycastFirstCellDown(b2dCmp.body.getPosition().x, elvCmp.fallTargetY, elvCmp.fallTargetElevation);
            if (v.b[2] != -1 && v.b[2] == elvCmp.fallTargetElevation) { //jumping at the same elevation
                return;
            }
            if (v.b[2] == -1) {                     //over an offWorld hole   handling may change
                elvCmp.fallTargetCell = null;
                elvCmp.fallTargetY = -1;
                elvCmp.fallTargetElevation = -1;

                return;
            }

            v = raycastFirstCellDown(b2dCmp.body.getPosition().x, elvCmp.fallTargetY, elvCmp.fallTargetElevation - 1);  //under the current elevation

            TiledMapTileLayer.Cell cell = v.a;
            int targetElevation = v.b[2];

            if (cell != null) {         //found a valid target lower than initial elevation
                float targetY = b2dCmp.body.getPosition().y - (elvCmp.elevation - targetElevation);

                elvCmp.fallTargetCell = cell;
                elvCmp.fallTargetY = targetY;
                elvCmp.fallTargetElevation = targetElevation;
            }
        }
    }


    /**
     * stops the entity from falling if it hits the fallTargets
     */
    private void fallArrive(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        if (typeCmp.entityState.equals(EntityState.fall)) {
            if (elvCmp.fallTargetCell != null || elvCmp.fallTargetY != TARGET_NOT_CALCULATED) {
                //fall to it
                // upon arrival state=idle or return to before falling if falling through map
                //place shadow on the target height
                if (b2dCmp.body.getPosition().y <= elvCmp.fallTargetY) {
                    typeCmp.entityState = EntityState.idle;
                    elvCmp.elevation = elvCmp.fallTargetElevation;
                    ComponentMappers.position().get(entity).elevation = elvCmp.fallTargetElevation;
                    b2dCmp.body.setTransform(b2dCmp.body.getPosition().x, elvCmp.fallTargetY, 0);

                    elvCmp.fallTargetY = TARGET_NOT_CALCULATED;
                    elvCmp.fallTargetCell = null;
                    elvCmp.fallTargetElevation = -1;
                    elvCmp.jumpFinished = true;

                }
            }
        }
    }

    /**
     * saves the entity's last known elevatio and position right before a fall or jump took place
     */
    private void saveStablePosition(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.fall)) {
            return;
        }
        elvCmp.lastStableElevation = elvCmp.elevation;
        elvCmp.lastStablePosition = b2dCmp.body.getPosition().cpy();
    }

    /**
     * handles the case in which an etity falls off the map
     */
    private void fallOffWorld(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (elvCmp.elevation < OFF_WORLD_FALL_DISTANCE) {
            b2dCmp.body.setTransform(elvCmp.lastStablePosition, 0);
            elvCmp.elevation = elvCmp.lastStableElevation;
            ComponentMappers.position().get(entity).elevation = elvCmp.lastStableElevation;
            elvCmp.fallTargetCell = null;
            elvCmp.fallTargetElevation = 0;
            typeCmp.entityState = EntityState.idle;
        }
    }


    //maybe move from here in an utility class
    public static Pair<TiledMapTileLayer.Cell, Integer[]> raycastFirstCellDown(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        return raycastFirstCellDown(b2dCmp.body.getPosition().x, b2dCmp.body.getPosition().y, elvCmp.elevation);
    }

    public static Pair<TiledMapTileLayer.Cell, Integer[]> raycastFirstCellDown(float fx, float fy, int startElevation) {//TODO use for rechecking falltarget

        for (int elevation = startElevation; elevation >= 0; elevation--) {
            MapLayers layers = MapManager.getLayersElevatedMap(elevation);
            if (layers == null) {
                continue;
            }
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {

                    float targetY = fy - (startElevation - elevation);
                    int x = Math.round(fx);
                    int y = Math.round(targetY);
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null) {
                        return new Pair<>(cell, new Integer[]{x, y, elevation});
                    }
                }
            }
        }
        return new Pair<>(null, new Integer[]{-1, -1, -1});
    }

    private TiledMapTileLayer.Cell getFirstCellUnderEntity(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        MapLayers layers = MapManager.getLayersElevatedMap(elvCmp.elevation);

        if (layers != null && layers.size() != 0) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {
                    return tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y));
                }
            }
        }
        return null;
    }

    /**
     * returns all cells raycast down that touch the rcast in the given radius ex radius=2, a list of cells in a circle of radius=2
     */
    public static List<Pair<TiledMapTileLayer.Cell, Integer[]>> raycastFirstCellsDownInRadius(Entity entity, int radius) {//TODO also do for up to check if hitting a ceiling
        return null;
    }

    private Pair<TiledMapTileLayer.Cell, Integer[]> raycastFirstCellUp(Entity entity) { //TODO
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        return null;
    }


    public static boolean isGrounded(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (elvCmp.jumpTaken &&
                !elvCmp.jumpFinished) {
            return false;
        }
        int elevation = elvCmp.elevation;
        MapLayers layers = MapManager.getLayersElevatedMap(elevation);

        if (layers != null && layers.size() != 0) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {

                    if (
                            tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                                    tileLayer.getCell((int) (b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                                    tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null ||
                                    tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
