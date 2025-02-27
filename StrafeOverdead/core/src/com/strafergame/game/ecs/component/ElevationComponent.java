package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.game.ecs.system.save.data.SaveableData;
import com.strafergame.game.ecs.system.world.ClimbFallSystem;

public class ElevationComponent implements Component, SaveableData<ElevationComponent> {


    /**
     * used only to filter collisions on different elevations
     */
    public int elevation = 0;

    /**
     * currently in the interactingEntities list of an elevation agent
     */
    public boolean isClimbing;

    /**
     * wether the entity is affected by gravity
     */
    public boolean gravity = true;

    /**
     * where the jump stops
     */
    public float jumpHeight = 0;


    public int jumpElevationDifference=0;
    /**
     * wether a jump is in effect at the time
     */
    public boolean jumpTaken = false;

    /**
     * whether it sits on ground
     */
    public boolean jumpFinished = true;

    /**
     * if affected by gravity this is the cell to which the entity falls atm
     */
    public TiledMapTileLayer.Cell fallTargetCell;

    /**
     * Y value for perspective of the taretCell
     */
    public float fallTargetY = ClimbFallSystem.TARGET_NOT_CALCULATED;

    /**
     * elevation at the target's layer
     */
    public int fallTargetElevation;
    /**
     * used to store y coordinates from meter to meter such that elevation can be changed while falling/jumping
     */
    public float prevIncrementalY;

    public Vector2 lastStablePosition;
    public int lastStableElevation;


    @Override
    public ElevationComponent copy() {
        ElevationComponent clone = new ElevationComponent();
        clone.elevation = elevation;
        clone.lastStablePosition = lastStablePosition.cpy();
        clone.lastStableElevation = lastStableElevation;
        return clone;
    }
}
