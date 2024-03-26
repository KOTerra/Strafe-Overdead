package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;

public class ElevationComponent implements Component {
    /**
     * used only to filter collisions on different elevations
     */
    public int elevation = 0;

    /**
     * wether the entity is affected by gravity
     */
   public boolean gravity = false;
}
