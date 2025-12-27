package com.strafergame.game.ecs.component.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

public class BehaviorTreeComponent implements Component {
    public BehaviorTree<Entity> tree;
}