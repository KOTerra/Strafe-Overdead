package com.strafergame.game.ecs.system.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
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
                            if (name.startsWith("collisions")) type = "COLLISION";
                            else if (name.startsWith("elevationAgents")) type = "ELEVATIONAGENT";
                            else if (name.startsWith("activators")) type = "ACTIVATOR";
                        }
                        
                        if (type == null) continue;
                        type = type.toUpperCase();

                        // Skip railings as enemies don't collide with them and should be able to path through them
                        if ("RAILING".equals(type)) continue;

                        int objElevation = object.getProperties().get("elevation", -1, Integer.class);
                        int baseElevation = object.getProperties().get("baseElevation", -1, Integer.class);
                        int topElevation = object.getProperties().get("topElevation", -1, Integer.class);
                        
                        // Check if this object is on the current graph elevation
                        boolean isElevationMatch = (objElevation != -1 && objElevation == elevation) || 
                                                   (baseElevation != -1 && baseElevation == elevation) ||
                                                   (topElevation != -1 && topElevation == elevation);
                        
                        // If no elevation properties found, default to 0
                        if (objElevation == -1 && baseElevation == -1 && topElevation == -1) {
                            isElevationMatch = (elevation == 0);
                        }

                        boolean isObstacleType = "COLLISION".equals(type) || "ELEVATIONAGENT".equals(type) || 
                                               "FOOTPRINT".equals(type) || "ACTIVATOR".equals(type) || "SLOPE".equals(type);
                        
                        if (isObstacleType && isElevationMatch) {
                            if (object instanceof RectangleMapObject rectObj) {
                                Rectangle rect = rectObj.getRectangle();
                                markObstacle(rect.x * sf, rect.y * sf, rect.width * sf, rect.height * sf);
                            } else if (object instanceof PolygonMapObject polyObj) {
                                float[] vertices = polyObj.getPolygon().getTransformedVertices();
                                float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
                                float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
                                for (int i = 0; i < vertices.length; i += 2) {
                                    float xV = vertices[i] * sf;
                                    float yV = vertices[i + 1] * sf;
                                    if (xV < minX) minX = xV;
                                    if (xV > maxX) maxX = xV;
                                    if (yV < minY) minY = yV;
                                    if (yV > maxY) maxY = yV;
                                }
                                markObstacle(minX, minY, maxX - minX, maxY - minY);
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
                
                // Diagonals (only if adjacent orthogonals are also traversable to avoid corner cutting)
                addDiagonalConnection(node, x + 1, y + 1, x + 1, y, x, y + 1);
                addDiagonalConnection(node, x - 1, y + 1, x - 1, y, x, y + 1);
                addDiagonalConnection(node, x + 1, y - 1, x + 1, y, x, y - 1);
                addDiagonalConnection(node, x - 1, y - 1, x - 1, y, x, y - 1);
            }
        }
    }

    private void markObstacle(float x, float y, float width, float height) {
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = (int) Math.ceil(x + width);
        int y2 = (int) Math.ceil(y + height);

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

    private void addConnection(AStarNode fromNode, int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            AStarNode toNode = nodes[y * width + x];
            if (toNode.traversable) {
                fromNode.getConnections().add(new DefaultConnection<>(fromNode, toNode));
            }
        }
    }

    private void addDiagonalConnection(AStarNode fromNode, int x, int y, int adjX1, int adjY1, int adjX2, int adjY2) {
        if (x >= 0 && x < width && y >= 0 && y < height &&
            adjX1 >= 0 && adjX1 < width && adjY1 >= 0 && adjY1 < height &&
            adjX2 >= 0 && adjX2 < width && adjY2 >= 0 && adjY2 < height) {
            
            AStarNode toNode = nodes[y * width + x];
            AStarNode adj1 = nodes[adjY1 * width + adjX1];
            AStarNode adj2 = nodes[adjY2 * width + adjX2];
            
            if (toNode.traversable && adj1.traversable && adj2.traversable) {
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

    public AStarNode getNearestTraversableNode(float x, float y) {
        AStarNode node = getNodeAtWorld(x, y);
        if (node != null && node.traversable) return node;

        // Search in increasing rings
        for (int r = 1; r < 5; r++) {
            for (int ix = -r; ix <= r; ix++) {
                for (int iy = -r; iy <= r; iy++) {
                    if (Math.abs(ix) != r && Math.abs(iy) != r) continue;
                    AStarNode n = getNodeAtWorld(x + ix, y + iy);
                    if (n != null && n.traversable) return n;
                }
            }
        }
        return null;
    }

    @Override
    public Array<Connection<AStarNode>> getConnections(AStarNode fromNode) {
        return fromNode.getConnections();
    }
}
