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
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.world.map.MapManager;

public class ClimbFallSystem extends IteratingSystem {

    public static final float JUMP_HEIGHT_DIFFERENCE = 3f;
    public static final int OFF_WORLD_FALL_DISTANCE = -10;
    public static final int TARGET_NOT_CALCULATED = -100;
    public static final String JUMPABLE_TAG = "jumpable";

    private final ClimbDelegate climbDelegate = new ClimbDelegate();
    private final JumpDelegate jumpDelegate = new JumpDelegate();
    private final FallDelegate fallDelegate = new FallDelegate();

    public ClimbFallSystem() {
        super(Family.all(ElevationComponent.class, Box2dComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        climbDelegate.climb(entity);

        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        // Only clamp elevation if we are stable (not falling/jumping) to avoid interfering with movement
        if (!isClimbing(entity) && (typeCmp == null || (!typeCmp.entityState.equals(EntityState.fall) && !typeCmp.entityState.equals(EntityState.jump)))) {
            climbDelegate.lowElevationClamping(entity);
        }

        // Only attempt to start a jump if we are in the jump state and not climbing
        if (jumpDelegate.canJump(entity) && !isClimbing(entity)) {
            jumpDelegate.beginJump(entity);
        }
        jumpDelegate.jumpArrive(entity);

        if (fallDelegate.shouldFall(entity) && !isClimbing(entity)) {    //change is climbing to not be true if just passed activator but not went up on elevation agent
            if (typeCmp != null) {
                typeCmp.entityState = EntityState.fall;
            }
            fallDelegate.computeFallTarget(entity);
        }
        fallDelegate.updateFallTarget(entity);

        fallDelegate.fallArrive(entity);
        fallDelegate.fallOffWorld(entity);
    }

    public static boolean isClimbing(Entity entity) {
        return ComponentMappers.elevation().get(entity).isClimbing;
    }

    /**
     * saves the entity's last known elevation and position right before a fall or jump took place
     */
    public static void saveStablePosition(Entity entity) {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(entity);
        if (typeCmp != null && (typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.fall))) {
            return;
        }
        elvCmp.lastStableElevation = elvCmp.elevation;
        elvCmp.lastStablePosition = b2dCmp.body.getPosition().cpy();
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
                if (layer instanceof TiledMapTileLayer tileLayer &&
                        (tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                                tileLayer.getCell((int) (b2dCmp.body.getPosition().x), Math.round(b2dCmp.body.getPosition().y)) != null ||
                                tileLayer.getCell(Math.round(b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null ||
                                tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y)) != null)
                ) {
                    return true;
                }
            }
        }
        return false;
    }
}
