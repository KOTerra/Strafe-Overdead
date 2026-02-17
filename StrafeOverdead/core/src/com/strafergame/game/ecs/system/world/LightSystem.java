package com.strafergame.game.ecs.system.world;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.LightComponent;

import java.nio.IntBuffer;

public class LightSystem extends IteratingSystem {

    private final RayHandler rayHandler;
    private final IntBuffer bufferHandle = BufferUtils.newIntBuffer(1);
    private final Array<Entity> lightEntities = new Array<>();

    public LightSystem(RayHandler rayHandler) {
        super(Family.all(LightComponent.class, PositionComponent.class).get());
        this.rayHandler = rayHandler;

        if (this.rayHandler != null) {
            this.rayHandler.setCulling(false);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LightComponent lightCmp = ComponentMappers.light().get(entity);
        PositionComponent posCmp = ComponentMappers.position().get(entity);

        if (!lightEntities.contains(entity, true)) {
            lightEntities.add(entity);
        }

        // Sync the component elevation with the entity elevation
        lightCmp.elevation = posCmp.elevation;

        for (LightComponent.LightSource source : lightCmp.lights) {
            if (source.light != null) {
                source.light.setPosition(
                        posCmp.renderPos.x + source.offset.x,
                        posCmp.renderPos.y + source.offset.y
                );
            }
        }
    }

    public void renderLightsForElevation(int elevation) {
        if (rayHandler == null) return;

        for (Entity entity : lightEntities) {
            LightComponent lightCmp = ComponentMappers.light().get(entity);

            // Check if this entity belongs to the elevation currently being rendered
            boolean active = (lightCmp.elevation == elevation);

            // Set activity for ALL lights on this entity
            for (LightComponent.LightSource source : lightCmp.lights) {
                if (source.light != null) {
                    source.light.setActive(active);
                }
            }
        }

        bufferHandle.clear();
        Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, bufferHandle);
        int currentFbo = bufferHandle.get(0);

        rayHandler.useCustomViewport(0, 0,
                Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight());

        rayHandler.setCombinedMatrix(Strafer.worldCamera);

        rayHandler.updateAndRender();

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFbo);
    }
}