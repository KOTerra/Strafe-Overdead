package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.articy.runtime.core.ArticyRuntime;
import com.articy.runtime.logic.ArticyVariableManager;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ArticyComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.articy.runtime.model.ArticyObject;

/**
 * A high-priority Ashley EntitySystem that listens for ArticyVariableManager changes
 * and updates entity components.
 */
public class ArticyMapperSystem extends IteratingSystem {

    public ArticyMapperSystem() {
        super(Family.all(ArticyComponent.class).get(), 1); // Priority 1 (High)
    }

    @Override
    public void update(float deltaTime) {
        ArticyVariableManager varManager = ArticyRuntime.getVariableManager();
        if (varManager == null) {
            return;
        }

        boolean globalDirty = varManager.isDirty();

        for (Entity entity : getEntities()) {
            ArticyComponent articyCmp = ComponentMappers.articy().get(entity);
            if (globalDirty || articyCmp.isDirty) {
                updateEntity(entity, articyCmp, varManager);
                articyCmp.isDirty = false;
            }
        }

        if (globalDirty) {
            varManager.clearDirty();
        }
    }

    private void updateEntity(Entity entity, ArticyComponent articyCmp, ArticyVariableManager varManager) {
        ArticyObject articyObj = ArticyRuntime.getDatabase().getObject(articyCmp.articyId, ArticyObject.class);
        if (articyObj == null) {
            return;
        }

        String techName = articyObj.getTechnicalName();
        if (techName != null && !techName.isEmpty()) {
            // Example: Toggling visibility based on NPC_TechName_Visible variable
            Object visible = varManager.getVariable("NPCState", techName + "_Visible");
            if (visible instanceof Boolean) {
                PositionComponent posCmp = ComponentMappers.position().get(entity);
                if (posCmp != null) {
                    posCmp.isHidden = !((Boolean) visible);
                }
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Handled in update for global optimization
    }
}
