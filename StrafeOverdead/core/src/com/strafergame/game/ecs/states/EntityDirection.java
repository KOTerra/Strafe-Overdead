package com.strafergame.game.ecs.states;

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
}
