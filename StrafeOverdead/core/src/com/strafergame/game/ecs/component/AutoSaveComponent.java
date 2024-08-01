package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.strafergame.game.ecs.system.save.SaveAction;

public class AutoSaveComponent<T> implements Component {
	public String key;
	public Class<T> objectType;
	public boolean saved;
	public SaveAction saveAction;
}
