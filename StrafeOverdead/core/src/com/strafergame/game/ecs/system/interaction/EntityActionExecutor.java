package com.strafergame.game.ecs.system.interaction;

import com.badlogic.ashley.core.Entity;

public interface EntityActionExecutor {
    boolean execute(Entity entity);
}
