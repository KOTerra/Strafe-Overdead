package com.strafergame.game.ecs.system.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AStarPathfinder {
    private final AStarGraph graph;
    private final IndexedAStarPathFinder<AStarNode> pathFinder;
    private final AStarHeuristic heuristic;

    public AStarPathfinder(int width, int height, int elevation) {
        this.graph = new AStarGraph(width, height, elevation);
        this.heuristic = new AStarHeuristic();
        
        IndexedGraph<AStarNode> indexedGraph = new IndexedGraph<>() {
            @Override
            public int getIndex(AStarNode node) {
                return node.getIndex(width);
            }

            @Override
            public int getNodeCount() {
                return width * height;
            }

            @Override
            public Array<Connection<AStarNode>> getConnections(AStarNode fromNode) {
                return fromNode.getConnections();
            }
        };

        this.pathFinder = new IndexedAStarPathFinder<>(indexedGraph);
    }

    public Array<Vector2> findPath(Vector2 start, Vector2 end) {
        AStarNode startNode = graph.getNearestTraversableNode(start.x, start.y);
        AStarNode endNode = graph.getNearestTraversableNode(end.x, end.y);

        if (startNode == null || endNode == null) {
            return null;
        }

        DefaultGraphPath<AStarNode> path = new DefaultGraphPath<>();
        if (pathFinder.searchNodePath(startNode, endNode, heuristic, path)) {
            Array<Vector2> waypoints = new Array<>();
            // Use current position as the start of the path to avoid "jumping" back to tile center
            waypoints.add(new Vector2(start.x, start.y));
            for (int i = 1; i < path.getCount(); i++) {
                AStarNode node = path.get(i);
                waypoints.add(new Vector2(node.x + 0.5f, node.y + 0.5f));
            }
            return waypoints;
        }
        return null;
    }

    public AStarGraph getGraph() {
        return graph;
    }
}
