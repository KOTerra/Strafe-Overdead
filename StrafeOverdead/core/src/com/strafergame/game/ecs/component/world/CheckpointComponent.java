package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.strafergame.game.ecs.system.save.CheckpointAction;

public class CheckpointComponent implements Component {
	public CheckpointAction action;
}
