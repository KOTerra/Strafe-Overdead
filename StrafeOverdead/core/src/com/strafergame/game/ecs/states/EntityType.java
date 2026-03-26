package com.strafergame.game.ecs.states;

/**
 * the type of an entity
 *
 * @author mihai_stoica
 */
public enum EntityType {

    player, goblin, dummy, checkpoint, npc, item, collision, elevationAgent;

    public static EntityType convert(String type) {
        if (type == null) {
            return null;
        }
        for (EntityType t : EntityType.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        return null;
    }

    public static boolean isEnemyOrNPC(EntityType t) {
        return t.equals(npc) || t.equals(goblin);
    }
}
