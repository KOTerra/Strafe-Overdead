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

    attack(true),

    /**
     * the RECOVER state after a hit
     */
    recover,
    /**
     * the DEATH state
     */
    death;

    private final boolean withSubstates;


    EntityState() {
        withSubstates = false;
    }

    EntityState(boolean withSubstates) {
        this.withSubstates = withSubstates;
    }

    public boolean isWithSubstates() {
        return withSubstates;
    }

    public interface EntitySubState {
        public default boolean isSubstate() {
            return !(this instanceof NoneSubstate);
        }

    }

    public enum NoneSubstate implements EntitySubState {
        none;

        @Override
        public String toString() {
            return "";
        }
    }

    public enum AttackSubstate implements EntitySubState {
        melee,
        shoot
    }
}

