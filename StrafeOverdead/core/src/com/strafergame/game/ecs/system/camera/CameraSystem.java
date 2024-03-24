package com.strafergame.game.ecs.system.camera;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.CameraComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.combat.ProximityContactPair;
import com.strafergame.game.world.GameWorld;
import com.strafergame.graphics.WorldCamera;

public class CameraSystem extends IteratingSystem {

    private WorldCamera cam = Strafer.worldCamera;

    public CameraSystem() {
        super(Family.all(CameraComponent.class, DetectorComponent.class).get());

    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DetectorComponent dtctrCmp = ComponentMappers.detector().get(entity);
        CameraComponent camCmp = ComponentMappers.camera().get(entity);
        PlayerComponent plyrCmp = ComponentMappers.player().get(GameWorld.player);

        if (ProximityContactPair.isPlayerInProximity(dtctrCmp)) {
            switch (camCmp.type) {
                case dummy: {

                    cam.addToFocus(entity);
                    cam.addToFocus(GameWorld.player);

                    break;
                }
                case checkpoint: {
                    cam.setFocusOn(entity);
                    break;
                }
                default:
                    break;
            }
        } else {
            if (plyrCmp.nearDetectors.size == 0) {
                cam.setFocusOn(GameWorld.player);
            }

        }

    }

}
