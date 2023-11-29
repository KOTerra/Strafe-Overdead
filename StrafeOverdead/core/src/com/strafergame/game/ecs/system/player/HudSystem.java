package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.HealthComponent;
import com.strafergame.game.ecs.component.PlayerComponent;

public class HudSystem extends IteratingSystem {

    public HudSystem() {
        super(Family.all(PlayerComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthCmp = ComponentMappers.health().get(entity);
        VisProgressBar healthBar = Strafer.uiManager.getHud().getHealthBar();
        healthBar.setValue(Interpolation.linear.apply(healthBar.getValue(), healthCmp.hitPoints, .05f));
    }

}
