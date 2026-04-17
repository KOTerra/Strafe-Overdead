package com.strafergame.game.ecs.system.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.world.map.MapManager;
import org.antlr.v4.runtime.misc.Pair;

public class MapQueryUtils {

    public static boolean isTileAt(int x, int y, int elevation) {
        MapLayers layers = MapManager.getLayersElevatedMap(elevation);
        if (layers != null) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {
                    if (tileLayer.getCell(x, y) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Pair<TiledMapTileLayer.Cell, Integer[]> raycastFirstCellDown(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);

        return raycastFirstCellDown(b2dCmp.body.getPosition().x, b2dCmp.body.getPosition().y, elvCmp.elevation, 0);
    }

    public static Pair<TiledMapTileLayer.Cell, Integer[]> raycastFirstCellDown(float fx, float fy, int startElevation, int downto) {
        for (int elevation = startElevation; elevation >= downto; elevation--) {
            MapLayers layers = MapManager.getLayersElevatedMap(elevation);
            if (layers == null) {
                continue;
            }
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {
                    float targetY = fy - (startElevation - elevation);
                    int x = Math.round(fx);
                    int y = Math.round(targetY);
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);

                    if (cell != null) {
                        return new Pair<>(cell, new Integer[]{x, y, elevation});
                    }
                }
            }
        }
        return new Pair<>(null, new Integer[]{-1, -1, -1});
    }

    public static TiledMapTileLayer.Cell getFirstCellUnderEntity(Entity entity) {
        ElevationComponent elvCmp = ComponentMappers.elevation().get(entity);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(entity);
        MapLayers layers = MapManager.getLayersElevatedMap(elvCmp.elevation);

        if (layers != null && layers.size() != 0) {
            for (MapLayer layer : layers) {
                if (layer instanceof TiledMapTileLayer tileLayer) {
                    return tileLayer.getCell((int) (b2dCmp.body.getPosition().x), (int) (b2dCmp.body.getPosition().y));
                }
            }
        }
        return null;
    }
}
