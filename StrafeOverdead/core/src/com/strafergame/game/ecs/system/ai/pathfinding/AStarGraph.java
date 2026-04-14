package com.strafergame.game.ecs.system.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.world.map.MapManager;

public class AStarGraph implements Graph<AStarNode> {
    public final int width;
    public final int height;
    private final AStarNode[] nodes;

    public AStarGraph(int width, int height, int elevation) {
        this.width = width;
        this.height = height;
        this.nodes = new AStarNode[width * height];

        MapLayers layers = MapManager.getLayersElevatedMap(elevation);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean traversable = false;
                if (layers != null) {
                    for (MapLayer layer : layers) {
                        if (layer instanceof TiledMapTileLayer tileLayer) {
                            if (tileLayer.getCell(x, y) != null) {
                                traversable = true;
                                break;
                            }
                        }
                    }
                }
                nodes[y * width + x] = new AStarNode(x, y, traversable);
            }
        }

        // Handle MapObject collisions
        TiledMap map = MapManager.getInstance().getMap();
        float sf = Strafer.SCALE_FACTOR;
        if (map != null) {
            for (MapLayer layer : map.getLayers()) {
                if (!(layer instanceof TiledMapTileLayer)) {
                    for (MapObject object : layer.getObjects()) {
                        String type = object.getProperties().get("type", String.class);
                        if (type == null) {
                            String name = layer.getName();
                            if (name.startsWith("collisions")) type = "collision";
                        }
                        
                        int objElevation = object.getProperties().get("elevation", 0, Integer.class);
                        if ("collision".equals(type) && objElevation == elevation) {
                            if (object instanceof RectangleMapObject rectObj) {
                                Rectangle rect = rectObj.getRectangle();
                                float startX = rect.x * sf;
                                float startY = rect.y * sf;
                                float endX = (rect.x + rect.width) * sf;
                                float endY = (rect.y + rect.height) * sf;
                                
                                // Calculate integer bounds for tiles touched by the rectangle
                                int x1 = (int) Math.floor(startX);
                                int y1 = (int) Math.floor(startY);
                                int x2 = (int) Math.ceil(endX);
                                int y2 = (int) Math.ceil(endY);
                                
                                // Mark the core area PLUS a 1-tile buffer in every direction
                                for (int ix = x1 - 1; ix <= x2; ix++) {
                                    for (int iy = y1 - 1; iy <= y2; iy++) {
                                        AStarNode node = getNode(ix, iy);
                                        if (node != null) {
                                            node.traversable = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add connections
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                AStarNode node = nodes[y * width + x];
                if (!node.traversable) continue;

                addConnection(node, x + 1, y);
                addConnection(node, x - 1, y);
                addConnection(node, x, y + 1);
                addConnection(node, x, y - 1);
                
                // Diagonals
                addConnection(node, x + 1, y + 1);
                addConnection(node, x - 1, y + 1);
                addConnection(node, x + 1, y - 1);
                addConnection(node, x - 1, y - 1);
            }
        }
    }

    private void addConnection(AStarNode fromNode, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            AStarNode toNode = nodes[y * width + x];
            if (toNode.traversable) {
                fromNode.getConnections().add(new DefaultConnection<>(fromNode, toNode));
            }
        }
    }

    public AStarNode getNode(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return nodes[y * width + x];
    }

    public AStarNode getNodeAtWorld(float x, float y) {
        return getNode((int) x, (int) y);
    }

    @Override
    public Array<Connection<AStarNode>> getConnections(AStarNode fromNode) {
        return fromNode.getConnections();
    }
}
