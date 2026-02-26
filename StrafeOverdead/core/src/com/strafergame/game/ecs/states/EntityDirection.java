package com.strafergame.game.ecs.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * the overall direction w,a,s or d
 */

public enum EntityDirection {
    w, s, a, d;

    public static EntityDirection convert(String str) {
        return switch (str) {
            case "w" -> EntityDirection.w;
            case "s" -> EntityDirection.s;
            case "a" -> EntityDirection.a;
            case "d" -> EntityDirection.d;
            default -> EntityDirection.w;
        };
    }

    public static Vector2 toVector2(EntityDirection direction) {
        return switch (direction) {
            case w -> new Vector2(0, 1);
            case s -> new Vector2(0, -1);
            case a -> new Vector2(-1, 0);
            case d -> new Vector2(1, 0);

        };
    }

    public static EntityDirection fromVector2(Vector2 vector) {
        if (Math.abs(vector.x) > Math.abs(vector.y)) {
            return vector.x > 0 ? EntityDirection.d : EntityDirection.a;
        }
        return vector.y > 0 ? EntityDirection.w : EntityDirection.s;
    }

}
