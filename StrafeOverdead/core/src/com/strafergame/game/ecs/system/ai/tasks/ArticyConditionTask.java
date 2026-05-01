package com.strafergame.game.ecs.system.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.articy.runtime.core.ArticyRuntime;

/**
 * A gdx-ai LeafTask that allows Behavior Trees to query ArticyVariableManager sets.
 */
public class ArticyConditionTask extends LeafTask<Entity> {

    public String variableSet;
    public String variableName;
    public Object expectedValue;

    @Override
    public Status execute() {
        if (ArticyRuntime.getVariableManager() == null) {
            return Status.FAILED;
        }

        Object value = ArticyRuntime.getVariableManager().getVariable(variableSet, variableName);
        if (expectedValue != null && expectedValue.equals(value)) {
            return Status.SUCCEEDED;
        }
        if (expectedValue == null && value == null) {
            return Status.SUCCEEDED;
        }
        
        return Status.FAILED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        ArticyConditionTask copy = (task != null) ? (ArticyConditionTask) task : new ArticyConditionTask();
        copy.variableSet = this.variableSet;
        copy.variableName = this.variableName;
        copy.expectedValue = this.expectedValue;
        return copy;
    }
}
