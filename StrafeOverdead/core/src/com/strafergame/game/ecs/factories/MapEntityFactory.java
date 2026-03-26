package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.world.MapLayerComponent;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.map.MapManager;

public abstract class MapEntityFactory {

    public static void createLayerEntity(MapLayer layer) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        if (layer instanceof TiledMapTileLayer) {
            PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
            SpriteComponent sprCmp = entityEngine.createComponent(SpriteComponent.class);
            MapLayerComponent layerCmp = entityEngine.createComponent(MapLayerComponent.class);
            sprCmp.sprite = null;
            posCmp.isMapLayer = true;

            posCmp.elevation = layer.getProperties().get("elevation", 0, Integer.class);

            MapManager.addLayerToElevation(layer, posCmp.elevation);


            layerCmp.layer = (TiledMapTileLayer) layer;

            entityEngine.addEntity(entityEngine.createEntity().add(posCmp).add(sprCmp).add(layerCmp));
        }
    }

    public static Entity createHitboxDummy(final Vector3 location, int width, int height, final Entity owner) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        final Entity dummy = entityEngine.createEntity();
        AttackComponent attckCmp = entityEngine.createComponent(AttackComponent.class);

        attckCmp.owner = owner;
        attckCmp.damagePerSecond = 10;
        attckCmp.doesKnockback = true;
        attckCmp.knockbackMagnitude = 2;
        Box2DFactory.createBodyWithHitbox(attckCmp, entityEngine.getBox2dWorld().getWorld(), width, height, location);
        dummy.add(attckCmp);
        entityEngine.addEntity(dummy);
        return dummy;
    }

}
