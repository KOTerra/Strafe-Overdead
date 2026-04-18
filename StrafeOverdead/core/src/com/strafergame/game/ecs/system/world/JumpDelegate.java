package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.collision.ElevationUtils;
import com.strafergame.game.world.map.MapManager;

import static com.strafergame.game.ecs.system.world.ClimbFallSystem.JUMP_HEIGHT_DIFFERENCE;

public class JumpDelegate {

    public boolean canJump(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity); //mYBE CHnange elevation at the start of the jump maybe intermediate state tryjump to check if canjump then change elevation and then move upwards

        if (typeCmp == null || !typeCmp.entityState.equals(EntityState.jump)) {
            return false;
        }
        if (!elvCmp.jumpFinished) {
            return false;
        }

        float targetX = b2dCmp.body.getPosition().x;
        float currentY = b2dCmp.body.getPosition().y;

        for (int eDiff = 1; eDiff <= (int) Math.ceil(JUMP_HEIGHT_DIFFERENCE); eDiff++) {
            int checkElevation = elvCmp.elevation + eDiff;
            MapLayers layers = MapManager.getLayersElevatedMap(checkElevation);

            if (layers != null && layers.size() != 0) {
                float checkY = currentY + eDiff;
                for (MapLayer layer : layers) {
                    if (layer instanceof TiledMapTileLayer tileLayer &&
                            tileLayer.getCell(Math.round(targetX), Math.round(checkY)) != null) {

                        final boolean[] isJumpable = {false};
                        b2dCmp.body.getWorld().QueryAABB(new QueryCallback() {
                            @Override
                            public boolean reportFixture(Fixture fixture) {
                                if (ClimbFallSystem.JUMPABLE_TAG.equals(fixture.getUserData())) {
                                    isJumpable[0] = true;
                                    return false;
                                }
                                return true;
                            }
                        }, targetX - 0.1f, checkY - 0.1f, targetX + 0.1f, checkY + 0.1f);

                        if (!isJumpable[0]) {
                            elvCmp.jumpHeight = checkY;
                            elvCmp.jumpElevationDifference = eDiff;
                            return true;
                        }
                    }
                }
            }
        }

        final boolean[] isBlocked = {false};
        Vector2 pos = b2dCmp.body.getPosition();
        Vector2 rayTarget = new Vector2(pos.x, pos.y + JUMP_HEIGHT_DIFFERENCE + 1);

        b2dCmp.body.getWorld().rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (!fixture.isSensor() && !ClimbFallSystem.JUMPABLE_TAG.equals(fixture.getUserData())) {
                    isBlocked[0] = true;
                    return 0;
                }
                return 1;
            }
        }, pos, rayTarget);

        if (isBlocked[0]) {
            //  lets jumpArrive handle the snap later.
            if (MapQueryUtils.isTileAt(Math.round(pos.x), Math.round(pos.y), elvCmp.elevation + 1)) {
                return true;
            }

            resetToIdle(entity);
            return false;
        }

        elvCmp.jumpHeight = currentY + JUMP_HEIGHT_DIFFERENCE;
        elvCmp.jumpElevationDifference = (int) Math.ceil(JUMP_HEIGHT_DIFFERENCE);
        return true;
    }

    private void resetToIdle(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        elvCmp.jumpHeight = 0f;
        if (typeCmp != null) {
            typeCmp.entityState = EntityState.idle;
        }
    }


    public void beginJump(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        if (typeCmp != null && typeCmp.entityState.equals(EntityState.jump) && !elvCmp.jumpTaken) {
            elvCmp.jumpTaken = true;
            elvCmp.jumpFinished = false;
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            MovementComponent movCmp = ComponentMappers.movement().get(entity);

            if (movCmp != null && movCmp.isMoving()) {
                b2dCmp.body.applyLinearImpulse(movCmp.dir.cpy().scl(0.5f), b2dCmp.body.getWorldCenter(), true);
            }

            elvCmp.prevIncrementalY = b2dCmp.body.getPosition().y;
            elvCmp.fallTargetY = b2dCmp.body.getPosition().y;
            elvCmp.fallTargetElevation = elvCmp.elevation;
            elvCmp.fallTargetCell = MapQueryUtils.getFirstCellUnderEntity(entity);
        }
    }

    public void jumpArrive(Entity entity) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp != null && typeCmp.entityState.equals(EntityState.jump)) {

            Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
            ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
            if (b2dCmp.body.getPosition().y >= elvCmp.jumpHeight) {
                ElevationUtils.changeElevation(entity, elvCmp.elevation + elvCmp.jumpElevationDifference);

                typeCmp.entityState = EntityState.fall;
                elvCmp.jumpHeight = 0f;
                elvCmp.jumpTaken = false;

            }
        }
    }
}
