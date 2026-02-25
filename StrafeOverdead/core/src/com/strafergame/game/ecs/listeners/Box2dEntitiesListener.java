package com.strafergame.game.ecs.listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;

public class Box2dEntitiesListener implements EntityListener {

    private final World world;

    public Box2dEntitiesListener(World world) {
        this.world = world;
    }

    @Override
    public void entityAdded(Entity entity) {
    }

    @Override
    public void entityRemoved(Entity entity) {
        //  standard Bodies
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        if (b2dCmp != null && b2dCmp.body != null) {
            destroyPhysicalBody(b2dCmp.body);
            b2dCmp.body = null;
        }

        //  Attack/Hitbox Bodies
        AttackComponent attckCmp = ComponentMappers.attack().get(entity);
        if (attckCmp != null && attckCmp.body != null) {
            destroyPhysicalBody(attckCmp.body);
            attckCmp.body = null;
        }
    }

    /**
     * Safely schedules the destruction of a Box2D body.
     */
    private void destroyPhysicalBody(final com.badlogic.gdx.physics.box2d.Body body) {
        Gdx.app.postRunnable(() -> {
            if (body.getWorld() != null) {
                world.destroyBody(body);
            }
        });
    }
}