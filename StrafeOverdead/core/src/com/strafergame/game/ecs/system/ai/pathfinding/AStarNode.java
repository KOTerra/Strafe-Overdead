package com.strafergame.game.ecs.system.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;

public class AStarNode {
    public final int x;
    public final int y;
    public boolean traversable;
    private final Array<Connection<AStarNode>> connections = new Array<>();

    public AStarNode(int x, int y, boolean traversable) {
        this.x = x;
        this.y = y;
        this.traversable = traversable;
    }

    public Array<Connection<AStarNode>> getConnections() {
        return connections;
    }

    public int getIndex(int width) {
        return y * width + x;
    }
}
