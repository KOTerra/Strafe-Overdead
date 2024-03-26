package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Pool;
import com.strafergame.game.ecs.states.ElevationAgentType;
import com.strafergame.game.ecs.states.EntityDirection;

/**
 * used on map entities with elevationCompomponents to operate on their elevation
 * ex. a slope slightly pushes an entity up or down as it moves alongside
 */
public class ElevationAgentComponent implements Component, Pool.Poolable {

    public ElevationAgentType type;

    public EntityDirection direction;

    /**
     * the height from which the slope begins
     */
    public int baseElevation;

    /**
     * the height to which it goes
     */
    public int topElevation;
    public Body footprintBody;
    public Body sensorBody;
    public Body baseActivator;
    public Body topActivator;


    @Override
    public void reset() {


    }
}
