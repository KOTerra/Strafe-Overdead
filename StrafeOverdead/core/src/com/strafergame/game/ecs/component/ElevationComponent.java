package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.strafergame.game.ecs.states.ElevationType;
import com.strafergame.game.ecs.states.EntityDirection;

/**
 * used on map entities to raise mover entities on forced perspective z axis
 * ex. a slope slightly pushes an entity up or down as it moves alongside
 */
public class ElevationComponent implements Component, Pool.Poolable {

    public Body footprint;
    public Body sensorBody;

    public ElevationType type;

    public EntityDirection direction;

    /**
     * the height from which the slope begins
     */
    public int baseHeight;

    /**
     * the height to which it goes
     */
    public int topHeight;

    @Override
    public void reset() {


    }
}
