package com.strafergame.game.ecs.states;

public enum ElevationType {
    SLOPE,
    ELEVATOR;

    public static ElevationType convert(String str) {
        return switch (str) {
            case "SLOPE" -> ElevationType.SLOPE;
            case "ELEVATOR" -> ElevationType.ELEVATOR;
            default -> ElevationType.ELEVATOR;
        };
    }
}
