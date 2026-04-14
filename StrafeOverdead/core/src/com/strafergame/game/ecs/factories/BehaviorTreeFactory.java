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

        // Death Branch
        Sequence<Entity> deathSequence = new Sequence<>();
        deathSequence.addChild(new IsDead());
        deathSequence.addChild(new DieAction());

        // Chase Branch
        Sequence<Entity> chaseSequence = new Sequence<>();
        chaseSequence.addChild(new IsPlayerNear());
        chaseSequence.addChild(new IsElevationMatching());
        chaseSequence.addChild(new ApproachTargetAStar());

        // Idle Branch
        IdleAction idleAction = new IdleAction();

        // Assemble Tree with priority
        rootSelector.addChild(deathSequence); // Check death first
        rootSelector.addChild(chaseSequence); // Then try to chase
        rootSelector.addChild(idleAction);    // Fallback to idle

        tree.addChild(rootSelector);
        return tree;
    }

    private BehaviorTreeFactory() {
    }
}