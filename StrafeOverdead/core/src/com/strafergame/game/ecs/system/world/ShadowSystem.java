package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.ShadowComponent;

public class ShadowSystem extends IteratingSystem {
    public ShadowSystem() {
        super(Family.all(ShadowComponent.class, PositionComponent.class, ElevationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        ShadowComponent shdCmp = ComponentMappers.shadow().get(entity);
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);


        shdCmp.position.set(posCmp.renderPos.x, posCmp.renderPos.y);

        if (elvCmp.fallTargetY != ClimbFallSystem.TARGET_NOT_CALCULATED) {
            shdCmp.position.set(posCmp.renderPos.x, elvCmp.fallTargetY);
        }
        if(shdCmp.position.y>posCmp.renderPos.y) {
            shdCmp.position.set(posCmp.renderPos.x, posCmp.renderPos.y);
        }

    }

}
