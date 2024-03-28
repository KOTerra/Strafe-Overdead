package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.strafergame.game.ecs.states.ActivatorType;

public class ActivatorComponent implements Component {
    public ActivatorType type;
    public Entity agent;

}
