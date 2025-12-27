package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.strafergame.game.ecs.system.ai.tasks.ApproachPlayer;
import com.strafergame.game.ecs.system.ai.tasks.IdleAction;
import com.strafergame.game.ecs.system.ai.tasks.IsElevationMatching;
import com.strafergame.game.ecs.system.ai.tasks.IsPlayerNear;

public class BehaviorTreeFactory {

    /**
     * Creates a standard NPC behavior tree:
     *  Try to Chase (if close and on same elevation)
     *  Otherwise Idle
     */
    public static BehaviorTree<Entity> createBasicNpcTree(Entity entity) {
        BehaviorTree<Entity> tree = new BehaviorTree<>();

        // Root of the tree is a Selector (The "OR" logic)
        // It tries the first child (Chase Logic). If that fails, it runs the second child (Idle).
        Selector<Entity> rootSelector = new Selector<>();

        // Branch 1: Chase Logic (Sequence = "AND" logic)
        // All conditions must be met for the action to happen
        Sequence<Entity> chaseSequence = new Sequence<>();
        chaseSequence.addChild(new IsPlayerNear());        // Condition 1
        chaseSequence.addChild(new IsElevationMatching()); // Condition 2
        chaseSequence.addChild(new ApproachPlayer());      // Action

        // Branch 2: Idle
        // Fallback if chase sequence fails
        IdleAction idleAction = new IdleAction();

        // Assemble Tree
        rootSelector.addChild(chaseSequence);
        rootSelector.addChild(idleAction);

        tree.addChild(rootSelector);

        return tree;
    }
}