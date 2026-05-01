package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.articy.runtime.core.ArticyRuntime;
import com.articy.runtime.model.ArticyObject;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.ArticyComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.ElevationComponent;
import com.strafergame.game.ecs.component.ai.BehaviorTreeComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.collision.Box2DFactory;

public class ArticyEntityFactory {

    public static Entity createEntity(long articyId, Vector3 position) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        Entity entity = entityEngine.createEntity();

        ArticyComponent articyCmp = entityEngine.createComponent(ArticyComponent.class);
        articyCmp.articyId = articyId;
        entity.add(articyCmp);

        PositionComponent posCmp = entityEngine.createComponent(PositionComponent.class);
        posCmp.renderPos = new Vector2(position.x, position.y);
        posCmp.elevation = (int) position.z;
        posCmp.isHidden = false;
        entity.add(posCmp);

        ElevationComponent elvCmp = entityEngine.createComponent(ElevationComponent.class);
        elvCmp.elevation = (int) position.z;
        entity.add(elvCmp);

        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.npc;
        entity.add(typeCmp);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/goblin_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;
        entity.add(spriteCmp);

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        Box2DFactory.createBody(b2dCmp, entityEngine.getBox2dWorld().getWorld(), 1f, 1f, 0f, 0f, new Vector2(position.x, position.y), BodyDef.BodyType.StaticBody);
        Box2DFactory.addHurtboxToBody(entityEngine.getBox2dWorld().getWorld(), b2dCmp, 1f, 1f, 0f, 0f);
        entity.add(b2dCmp);

        // Metadata-driven component attachment
        ArticyObject articyObj = ArticyRuntime.getDatabase().getObject(articyId, ArticyObject.class);
        if (articyObj != null) {
            String techName = articyObj.getTechnicalName();
            if (techName != null) {
                // Check initial visibility from Articy variables
                Object visible = ArticyRuntime.getVariableManager().getVariable("NPCState", techName + "_Visible");
                if (visible instanceof Boolean) {
                    posCmp.isHidden = !((Boolean) visible);
                }

                if (techName.startsWith("NPC_")) {
                    BehaviorTreeComponent btCmp = entityEngine.createComponent(BehaviorTreeComponent.class);
                    btCmp.tree = BehaviorTreeFactory.createBasicNpcTree(entity);
                    entity.add(btCmp);
                }
            }
        }

        entityEngine.addEntity(entity);
        return entity;
    }
}
