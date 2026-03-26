package com.strafergame.game.ecs.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector3;
import com.strafergame.Strafer;
import com.strafergame.assets.AnimationProvider;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.*;
import com.strafergame.game.ecs.component.world.ShadowComponent;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.game.ecs.system.save.data.PlayerSaveData;
import com.strafergame.game.world.collision.Box2DFactory;
import com.strafergame.game.world.collision.FilteredContactListener;

public class PlayerFactory implements EntityCreator {
    @Override
    public Entity create(Vector3 position, MapObject mapObject) {
        EntityEngine entityEngine = EntityEngine.getInstance();
        final Entity player = entityEngine.createEntity();

        PlayerSaveData playerSaveData = SaveSystem.getPlayerSaveData();

        AutoSaveComponent asvCmp = entityEngine.createComponent(AutoSaveComponent.class);
        asvCmp.saveAction = () -> {
            playerSaveData.register();
            SaveSystem.getCurrentSave().serialize();
        };
        player.add(asvCmp);

        playerSaveData.setPlayer(player);
        playerSaveData.retrieve();
        playerSaveData.loadOwner();

        //deserialized
        StatsComponent statsCmp = playerSaveData.getStatsCmp();
        PositionComponent posCmp = playerSaveData.getPosCmp();
        //deserialized

        EntityTypeComponent typeCmp = entityEngine.createComponent(EntityTypeComponent.class);
        typeCmp.entityType = EntityType.player;
        player.add(typeCmp);

        PlayerComponent plyrCmp = entityEngine.createComponent(PlayerComponent.class);
        player.add(plyrCmp);

        SpriteComponent spriteCmp = entityEngine.createComponent(SpriteComponent.class);
        player.add(spriteCmp);
        spriteCmp.sprite = new Sprite(Strafer.assetManager.get("images/player_static.png", Texture.class));
        spriteCmp.height = spriteCmp.sprite.getHeight() * Strafer.SCALE_FACTOR;
        spriteCmp.width = spriteCmp.sprite.getWidth() * Strafer.SCALE_FACTOR;

        AnimationComponent aniCmp = entityEngine.createComponent(AnimationComponent.class);
        aniCmp.animation = AnimationProvider.getAnimation(player);
        player.add(aniCmp);

        ShadowComponent shdCmp = entityEngine.createComponent(ShadowComponent.class);
        shdCmp.radius = aniCmp.animation.getKeyFrame(0).getWidth() * .5f;
        player.add(shdCmp);

        //dependant on serialization
        MovementComponent movCmp = entityEngine.createComponent(MovementComponent.class);
        movCmp.maxLinearSpeed = statsCmp.baseSpeed;
        movCmp.dashDuration = statsCmp.dashDuration;
        movCmp.dashForce = statsCmp.dashForce;
        player.add(movCmp);

        Box2dComponent b2dCmp = entityEngine.createComponent(Box2dComponent.class);
        player.add(b2dCmp);
        EntityFactory.initPhysics(player);
        plyrCmp.sensor = Box2DFactory.createRadialSensor(b2dCmp.body, FilteredContactListener.DETECTOR_RADIUS,
                FilteredContactListener.PLAYER_CATEGORY, FilteredContactListener.PLAYER_DETECTOR_CATEGORY);
        b2dCmp.body.setUserData(player);
        b2dCmp.body.setTransform(posCmp.renderPos, 0);

        player.add(entityEngine.createComponent(SteeringComponent.class).setOwner(player));

        entityEngine.addEntity(player);
        return player;
    }
}
