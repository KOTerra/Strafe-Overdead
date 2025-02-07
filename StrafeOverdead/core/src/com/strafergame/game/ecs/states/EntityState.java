package com.strafergame.game.ecs.states;

/**
 * a state in which an entity can be at a given time
 *
 * @author mihai_stoica
 */
public enum EntityState {
    /**
     * the IDLE state
     */
    idle,
    /**
     * the WALK state
     */
    walk,
    /**
     * the RUN state
     */
    run,
    /**
     * jumping state
     */
    jump,
    /**
     * falling
     */
    fall,
    /**
     * the DASH state
     */
    dash,
    /**
     * the HIT state
     */
    hit,
    /**
     * the RECOVER state after a hit
     */
    recover,
    /**
     * the DEATH state
     */
    death;

    public static boolean isGrounded(EntityState state) {
        return switch (state) {
            case jump, fall, dash, hit -> false;
            default -> true;
        };
    }

}
