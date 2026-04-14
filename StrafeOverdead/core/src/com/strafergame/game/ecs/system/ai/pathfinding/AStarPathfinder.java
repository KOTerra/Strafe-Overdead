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
        
        // Custom IndexedGraph implementation since AStarGraph is just a Graph
        IndexedGraph<AStarNode> indexedGraph = new IndexedGraph<AStarNode>() {
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
            for (AStarNode node : path) {
                waypoints.add(new Vector2(node.x + 0.5f, node.y + 0.5f));
            }
            return waypoints;
        }
        return null;
    }
}
