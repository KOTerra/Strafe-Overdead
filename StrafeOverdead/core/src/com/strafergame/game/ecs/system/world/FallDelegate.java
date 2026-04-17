package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.collision.FilteredContactListener;
import com.strafergame.game.world.map.MapManager;
import org.antlr.v4.runtime.misc.Pair;

import static com.strafergame.game.ecs.system.world.ClimbFallSystem.OFF_WORLD_FALL_DISTANCE;
import static com.strafergame.game.ecs.system.world.ClimbFallSystem.TARGET_NOT_CALCULATED;

public class FallDelegate {

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
                            tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null
                    ) {
                        ClimbFallSystem.saveStablePosition(entity);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void computeFallTarget(Entity entity) {         //solve fall after jumping in null layer, maybe add an empty layer on top of the map, handled automatically

        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Pair<TiledMapTileLayer.Cell, Integer[]> raycastPair = MapQueryUtils.raycastFirstCellDown(entity);
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

            float currentY = b2dCmp.body.getPosition().y;
            float targetY = currentY - (elvCmp.elevation - elevation);

            // Force at least -1.0f on the body if the calculated target is too close
            if (currentY - targetY <= 1.0f) {
                b2dCmp.body.setTransform(b2dCmp.body.getPosition().x, currentY - 1f, 0);
            }

            elvCmp.elevation -= 1;
            ComponentMappers.position().get(entity).elevation -= 1; ///if falling in w direction starts with an elevation down already
            FilteredContactListener.setShadowFilter(b2dCmp.body, elvCmp.elevation);

            elvCmp.prevIncrementalY = currentY;
            elvCmp.fallTargetCell = cell;
            elvCmp.fallTargetY = targetY;
            elvCmp.fallTargetElevation = elevation;
        }
    }

    public void updateFallTarget(Entity entity) {  //TODO have to also update if a higher elevation cell comes between target and entity
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);


        if (elvCmp.fallTargetCell != null) {
            TiledMapTileLayer.Cell cell;
            int targetElevation;
            Pair<TiledMapTileLayer.Cell, Integer[]> v;

            v = MapQueryUtils.raycastFirstCellDown(b2dCmp.body.getPosition().x, b2dCmp.body.getPosition().y, elvCmp.elevation - 1, elvCmp.fallTargetElevation + 1);  //over the current elevation
            cell = v.a;
            targetElevation = v.b[2];
            if (cell != null) {         //found a valid target over the initial elevation
                float targetY = b2dCmp.body.getPosition().y - (elvCmp.elevation - targetElevation);

                elvCmp.fallTargetCell = cell;
                elvCmp.fallTargetY = targetY + 1;//TODO maybe not -1
                elvCmp.fallTargetElevation = targetElevation;
                return;
            }

            v = MapQueryUtils.raycastFirstCellDown(b2dCmp.body.getPosition().x, b2dCmp.body.getPosition().y, elvCmp.fallTargetElevation, 0);
            if (v.b[2] != -1 && v.b[2] == elvCmp.fallTargetElevation) { //jumping at the same elevation
                return;
            }
            if (v.b[2] == -1) {                     //over an offWorld hole   handling may change
                elvCmp.fallTargetCell = null;
                elvCmp.fallTargetY = -1;
                elvCmp.fallTargetElevation = -1;

                return;
            }


            v = MapQueryUtils.raycastFirstCellDown(b2dCmp.body.getPosition().x, b2dCmp.body.getPosition().y, elvCmp.fallTargetElevation - 1, 0);  //under the current elevation
            cell = v.a;
            targetElevation = v.b[2];
            if (cell != null) {         //found a valid target lower than initial elevation
                float targetY = b2dCmp.body.getPosition().y - (elvCmp.elevation - targetElevation);

                elvCmp.fallTargetCell = cell;
                elvCmp.fallTargetY = targetY;
                elvCmp.fallTargetElevation = targetElevation;
            }
        }
    }

    public void fallArrive(Entity entity) {
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
                    ComponentMappers.position().get(entity).elevation = elvCmp.elevation;
                    FilteredContactListener.setShadowFilter(b2dCmp.body, elvCmp.elevation);

                    b2dCmp.body.setTransform(b2dCmp.body.getPosition().x, elvCmp.fallTargetY, 0);

                    elvCmp.fallTargetY = TARGET_NOT_CALCULATED;
                    elvCmp.fallTargetCell = null;
                    elvCmp.fallTargetElevation = -1;
                    elvCmp.jumpFinished = true;

                }
            }
        }
    }

    public void fallOffWorld(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (elvCmp.elevation < OFF_WORLD_FALL_DISTANCE) {
            b2dCmp.body.setTransform(elvCmp.lastStablePosition, 0);
            elvCmp.elevation = elvCmp.lastStableElevation;
            ComponentMappers.position().get(entity).elevation = elvCmp.lastStableElevation;
            FilteredContactListener.setShadowFilter(b2dCmp.body, elvCmp.elevation);

            elvCmp.fallTargetCell = null;
            elvCmp.fallTargetElevation = 0;
            typeCmp.entityState = EntityState.idle;
        }
    }
}
