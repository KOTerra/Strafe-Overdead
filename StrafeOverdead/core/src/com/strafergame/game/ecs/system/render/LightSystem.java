package com.strafergame.game.ecs.system.render;

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
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.LightComponent;
import com.strafergame.game.world.collision.FilteredContactListener;

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

        lightCmp.elevation = posCmp.elevation;

        for (LightComponent.LightSource source : lightCmp.lights) {
            if (source.light != null) {
                source.light.setPosition(posCmp.renderPos.x + source.offset.x, posCmp.renderPos.y + source.offset.y);

                //  which wall elevation bit this light hits
                short mask = FilteredContactListener.getWallCategory(lightCmp.elevation);
                // Set light category to -1 so it is never ignored by object maskBits
                source.light.setContactFilter((short) -1, (short) 0, mask);
            }
        }
    }

    public void renderLightsForElevation(int elevation) {
        if (rayHandler == null) return;

        for (Entity entity : lightEntities) {
            if (!EntityEngine.getInstance().getEntities().contains(entity, true)) {
                lightEntities.removeValue(entity, true);
                continue;
            }
            LightComponent lightCmp = ComponentMappers.light().get(entity);
            boolean isActive = (lightCmp.elevation == elevation);
            for (LightComponent.LightSource source : lightCmp.lights) {
                if (source.light != null) {
                    source.light.setActive(isActive);
                }
            }
        }

        bufferHandle.clear();
        Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, bufferHandle);
        int currentFbo = bufferHandle.get(0);
        rayHandler.useCustomViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        rayHandler.setCombinedMatrix(Strafer.worldCamera);
        rayHandler.updateAndRender();
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFbo);
    }
}