package com.strafergame.game.ecs.states;

public enum ElevationAgentType {
    SLOPE,
    ELEVATOR;

    public static ElevationAgentType convert(String str) {
        return switch (str) {
            case "SLOPE" -> ElevationAgentType.SLOPE;
            case "ELEVATOR" -> ElevationAgentType.ELEVATOR;
            default -> ElevationAgentType.ELEVATOR;
        };
    }
}
