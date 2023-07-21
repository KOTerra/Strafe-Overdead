package com.strafergame.game.ecs.system.save;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AutoSaveComponent;

public class AutoSaveSystem extends IntervalIteratingSystem {

	public AutoSaveSystem(float interval) {
		super(Family.all(AutoSaveComponent.class).get(), interval);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void processEntity(final Entity entity) {
		AutoSaveComponent svCmp = ComponentMappers.save().get(entity);
		svCmp.saved = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				AutoSaveComponent svCmp = ComponentMappers.save().get(entity);
				GdxPreferencesSerializer.saveToPreferences(svCmp.key, entity, svCmp.objectType);
				svCmp.saved = true;
			}
		}).start();
	}

}
