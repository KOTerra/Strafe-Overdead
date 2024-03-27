package com.strafergame.game.ecs.system.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ComponentDataUtils;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.states.EntityType;

public class ProximityContact {
    public Fixture sensor;
    public Fixture detector;

    public ProximityContact(Fixture sensor, Fixture detector) {
        this.sensor = sensor;
        this.detector = detector;
    }

    /**
     * @return the entity of the detector if it is in proximity of the player
     */
    public static Entity getEntityInProximity(DetectorComponent dtctrCmp) {
        ProximityContact contact = (ProximityContact) dtctrCmp.detector.getUserData();
        if (contact != null) {
            return ComponentDataUtils.getEntityFrom(contact.detector);
        }
        return null;
    }

    public static Entity getPlayerInProximity(DetectorComponent dtctrCmp) {
        Entity entity = getEntityInProximity(dtctrCmp);
        if (entity != null) {
            EntityTypeComponent entityTypeComponent = ComponentMappers.entityType().get(entity);
            if (entityTypeComponent != null) {
                if (entityTypeComponent.entityType.equals(EntityType.player)) {
                    return entity;
                }
            }
        }
        return null;
    }

    public static boolean isPlayerInProximity(DetectorComponent dtctrCmp) {
        return getEntityInProximity(dtctrCmp) != null;
    }

    public static boolean isPlayerInProximity(ProximityContact contact) {
        return contact != null;
    }

}
