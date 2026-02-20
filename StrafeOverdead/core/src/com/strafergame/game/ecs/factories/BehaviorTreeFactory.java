package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.strafergame.game.ecs.system.ai.tasks.*;

public class BehaviorTreeFactory {

    /**
     * Creates a standard NPC behavior tree:
     * Try to Chase (if close and on same elevation)
     * Otherwise Idle
     */
    public static BehaviorTree<Entity> createBasicNpcTree(Entity entity) {
        BehaviorTree<Entity> tree = new BehaviorTree<>();
        Selector<Entity> rootSelector = new Selector<>();

        // --- Branch 0: Death (Priority #1) ---
        Sequence<Entity> deathSequence = new Sequence<>();
        deathSequence.addChild(new IsDead());
        deathSequence.addChild(new DieAction());

        // --- Branch 1: Chase Logic ---
        Sequence<Entity> chaseSequence = new Sequence<>();
        chaseSequence.addChild(new IsPlayerNear());
        chaseSequence.addChild(new IsElevationMatching());
        chaseSequence.addChild(new ApproachPlayer());

        // --- Branch 2: Idle ---
        IdleAction idleAction = new IdleAction();

        // Assemble Tree (Order matters!)
        rootSelector.addChild(deathSequence); // Check death first
        rootSelector.addChild(chaseSequence); // Then try to chase
        rootSelector.addChild(idleAction);    // Fallback to idle

        tree.addChild(rootSelector);
        return tree;
    }
}