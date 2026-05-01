package com.strafergame.game.ecs.system.save.data;

import com.articy.runtime.core.ArticyRuntime;
import com.strafergame.game.ecs.system.save.SaveSystem;
import java.util.Map;

/**
 * Persistence data for Articy world state and narrative progress.
 */
public class ArticySaveData implements SaveData, SaveableData<ArticySaveData> {

    public Map<String, Map<String, Object>> variables;
    public long currentNodeId;
    public static final String ARTICY_KEY = "ARTICY_DATA";

    @Override
    public void register() {
        if (ArticyRuntime.getVariableManager() != null) {
            this.variables = ArticyRuntime.getVariableManager().getVariableSets();
        }
        if (ArticyRuntime.getFlowPlayer() != null) {
            this.currentNodeId = ArticyRuntime.getFlowPlayer().getCurrentPausedObjectId();
        }
        SaveSystem.getCurrentSave().register(ARTICY_KEY, this, ArticySaveData.class);
    }

    @Override
    public void retrieve() {
        ArticySaveData data = SaveSystem.retrieveFromRecords(ARTICY_KEY);
        if (data != null) {
            this.variables = data.variables;
            this.currentNodeId = data.currentNodeId;

            if (ArticyRuntime.getVariableManager() != null && variables != null) {
                ArticyRuntime.getVariableManager().restoreState(variables);
            }
            if (ArticyRuntime.getFlowPlayer() != null) {
                ArticyRuntime.getFlowPlayer().restoreState(currentNodeId);
            }
        }
    }

    @Override
    public void loadOwner() {
        // No physical owner for global Articy state
    }

    @Override
    public void invalidate() {
        this.variables = null;
        this.currentNodeId = -1L;
    }

    @Override
    public ArticySaveData copy() {
        ArticySaveData copy = new ArticySaveData();
        copy.variables = this.variables;
        copy.currentNodeId = this.currentNodeId;
        return copy;
    }
}
