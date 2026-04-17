package com.strafergame.game.ecs.factories;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.LightComponent;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.ElevationUtils;

public abstract class EntityFactory {

    public static void initPhysics(Entity e) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
        PositionComponent posCmp = ComponentMappers.position().get(e);
        SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

        posCmp.prevPos.x = -spriteCmp.width / 2;
        posCmp.prevPos.y = -spriteCmp.height / 2;

        Box2DFactory.createBody(b2dCmp, entityEngine.getBox2dWorld().getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
                posCmp.prevPos, BodyDef.BodyType.DynamicBody);
        Box2DFactory.addHurtboxToBody(entityEngine.getBox2dWorld().getWorld(), b2dCmp, spriteCmp.width, spriteCmp.height, 0,
                spriteCmp.height / 2);

        b2dCmp.body.setUserData(e);

        // Add shadow casting bit for this elevation without overwriting HURTBOX bits.
        short shadowBit = ElevationUtils.getWallCategory(posCmp.elevation);
        for (Fixture fixture : b2dCmp.body.getFixtureList()) {
            if (!fixture.isSensor()) {
                Filter filter = fixture.getFilterData();
                filter.categoryBits |= shadowBit;
                fixture.setFilterData(filter);
            }
        }


        // Apply elevation shadow bit to the player's solid body fixtures
        ElevationUtils.setShadowFilter(b2dCmp.body, posCmp.elevation);

        b2dCmp.initiatedPhysics = true;
    }

    /**
     * Helper to attach a light to an entity
     */
    public static void attachLight(Entity entity, Vector2 offset, float distance, Color color, int rays) {
        LightComponent lightCmp = ComponentMappers.light().get(entity);
        if (lightCmp == null) {
            lightCmp = EntityEngine.getInstance().createComponent(LightComponent.class);
            entity.add(lightCmp);
        }

        RayHandler rayHandler = EntityEngine.getInstance().getRayHandler();
        PointLight pointLight = new PointLight(rayHandler, rays, color, distance, 0, 0);

        lightCmp.lights.add(new LightComponent.LightSource(pointLight, offset));
    }
}
