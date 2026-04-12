package com.strafergame.game.ecs.system.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class AStarHeuristic implements Heuristic<AStarNode> {
    @Override
    public float estimate(AStarNode node, AStarNode endNode) {
        // Manhattan distance as heuristic
        return Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y);
    }
}
