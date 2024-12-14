package com.strafergame.game.ecs.system.save.data;

import com.badlogic.ashley.core.Entity;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.system.save.SaveSystem;

/**
 * implementation of the SaveData interface that links the player's save data to the player Entity
 */
public class PlayerSaveData implements SaveData {

    Entity player;

    private StatsComponent statsCmp;
    private PositionComponent posCmp;
    private ElevationComponent elvCmp;
    private HealthComponent healthCmp;


    @Override
    public void retrieve() {
        statsCmp = SaveSystem.retrieveComponentFromRecords("PLAYER_STATS_COMPONENT", StatsComponent.class);
        posCmp = SaveSystem.retrieveComponentFromRecords("PLAYER_POSITION_COMPONENT", PositionComponent.class);
        elvCmp = SaveSystem.retrieveComponentFromRecords("PLAYER_ELEVATION_COMPONENT", ElevationComponent.class);
        healthCmp = SaveSystem.retrieveComponentFromRecords("PLAYER_HEALTH_COMPONENT", HealthComponent.class);
    }

    @Override
    public void register() {
        SaveSystem.getCurrentSave().register("PLAYER_STATS_COMPONENT", statsCmp, StatsComponent.class);
        SaveSystem.getCurrentSave().register("PLAYER_POSITION_COMPONENT", posCmp, PositionComponent.class);
        SaveSystem.getCurrentSave().register("PLAYER_ELEVATION_COMPONENT", elvCmp, ElevationComponent.class);
        SaveSystem.getCurrentSave().register("PLAYER_HEALTH_COMPONENT", healthCmp, HealthComponent.class);
    }

    @Override
    public void loadOwner() {
        player.add(statsCmp);
        player.add(posCmp);
        player.add(elvCmp);
        player.add(healthCmp);

        updatePostLoad();
    }

    /**
     * updates the player components that are not serialized but depend on the ones that are
     */
    private void updatePostLoad() {
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(player);
        MovementComponent movCmp = ComponentMappers.movement().get(player);

        if (b2dCmp != null) {
            b2dCmp.body.setTransform(posCmp.renderPos, 0);
        }

        if (movCmp != null) {
            movCmp.maxLinearSpeed = statsCmp.baseSpeed;
            movCmp.dashDuration = statsCmp.dashDuration;
            movCmp.dashForce = statsCmp.dashForce;
        }
    }

    @Override
    public void invalidate() {
        player.remove(StatsComponent.class);
        player.remove(PositionComponent.class);
        player.remove(ElevationComponent.class);
        player.remove(HealthComponent.class);
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Entity getPlayer() {
        return player;
    }

    public StatsComponent getStatsCmp() {
        return statsCmp;
    }

    public PositionComponent getPosCmp() {
        return posCmp;
    }

    public ElevationComponent getElvCmp() {
        return elvCmp;
    }

    public HealthComponent getHealthCmp() {
        return healthCmp;
    }
}
