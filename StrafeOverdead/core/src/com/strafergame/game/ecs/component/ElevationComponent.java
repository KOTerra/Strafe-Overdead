package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class ElevationComponent implements Component {
    /**
     * used only to filter collisions on different elevations
     */
    public int elevation = 0;

    /**
     * wether the entity is affected by gravity
     */
    public boolean gravity = false;

    /**
     * if affected by gravity this is the cell to which the entity falls atm
     */
    public TiledMapTileLayer.Cell fallTargetCell;

    /**
     * Y value for perspective of the taretCell
     */
    public int fallTargetY;

    /**
     * elevation at the target's layer
     */
    public int fallTargetElevation;
}
