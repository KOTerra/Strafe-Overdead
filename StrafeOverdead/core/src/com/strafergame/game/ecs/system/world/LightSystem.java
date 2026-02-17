package com.strafergame.game.ecs.system.world;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.LightComponent;
import com.strafergame.game.world.collision.FilteredContactListener;

import java.nio.IntBuffer;

public class LightSystem extends IteratingSystem {

    private final RayHandler rayHandler;
    private final IntBuffer bufferHandle = BufferUtils.newIntBuffer(1);

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

        if (lightCmp.light != null) {
            lightCmp.light.setPosition(posCmp.renderPos.x + lightCmp.offset.x, posCmp.renderPos.y + lightCmp.offset.y);
            lightCmp.elevation = posCmp.elevation;

        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (rayHandler == null) return;

        bufferHandle.clear();
        Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, bufferHandle);
        int currentFbo = bufferHandle.get(0);


        rayHandler.useCustomViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());

        rayHandler.setCombinedMatrix(Strafer.worldCamera);

        rayHandler.updateAndRender();

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, currentFbo);
    }
}