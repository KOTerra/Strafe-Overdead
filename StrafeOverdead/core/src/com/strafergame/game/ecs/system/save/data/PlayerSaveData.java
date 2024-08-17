package com.strafergame.game.ecs.system.save.data;

import com.badlogic.ashley.core.Entity;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.system.save.SaveSystem;

/**
 * implementation of the SaveData interface that links the player's save data to the player Entity
 */
public class PlayerSaveData implements SaveData {

    Entity player;

    StatsComponent statsCmp;
    PositionComponent posCmp;
    ElevationComponent elvCmp;
    HealthComponent healthCmp;

    @Override
    public void retrieve() {
        statsCmp = SaveSystem.retrieveFromRecords("PLAYER_STATS_COMPONENT", EntityEngine.getInstance().createComponent(StatsComponent.class));
        posCmp = SaveSystem.retrieveFromRecords("PLAYER_POSITION_COMPONENT", EntityEngine.getInstance().createComponent(PositionComponent.class));
        elvCmp = SaveSystem.retrieveFromRecords("PLAYER_ELEVATION_COMPONENT", EntityEngine.getInstance().createComponent(ElevationComponent.class));
        healthCmp = SaveSystem.retrieveFromRecords("PLAYER_HEALTH_COMPONENT", EntityEngine.getInstance().createComponent(HealthComponent.class));
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
