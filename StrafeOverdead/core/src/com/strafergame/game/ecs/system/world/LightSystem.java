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

        if (lightCmp.light != null) {
            lightCmp.light.setPosition(posCmp.renderPos.x + lightCmp.offset.x, posCmp.renderPos.y + lightCmp.offset.y);
            lightCmp.elevation = posCmp.elevation;
        }
    }

    public void renderLightsForElevation(int elevation) {
        if (rayHandler == null) return;

        // Activate only lights on this elevation
        for (Entity entity : lightEntities) {
            LightComponent lightCmp = ComponentMappers.light().get(entity);
            if (lightCmp.light != null) {
                // If you want lights to affect ALL layers below them, change logic here.
                // Currently: strict equality (Light Layer 1 only affects Sprite Layer 1)
                lightCmp.light.setActive(lightCmp.elevation == elevation);
            }
        }

        // Standard Box2DLights setup
        bufferHandle.clear();
        Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, bufferHandle);
        int currentFbo = bufferHandle.get(0);

        rayHandler.useCustomViewport(0, 0,
                Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight());

        rayHandler.setCombinedMatrix(Strafer.worldCamera);

        // Render light + ambient.
        // Because of Stencil, this will ONLY draw on the pixels of the current elevation.
        rayHandler.updateAndRender();

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFbo);
    }
}