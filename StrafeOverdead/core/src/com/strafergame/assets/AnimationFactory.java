package com.strafergame.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.states.EntityType;

import static com.strafergame.assets.AnimationProvider.*;

public abstract class AnimationFactory {
    public static void prepareAnimations() {
        TYPE_ANIMATIONS.get(EntityType.player).put("idle_w", makeSprites(0.25f, EntityType.player, "idle_w", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("idle_a", makeSprites(0.25f, EntityType.player, "idle_a", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("idle_s", makeSprites(0.25f, EntityType.player, "idle_s", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("idle_d", makeSprites(0.25f, EntityType.player, "idle_d", true));

        TYPE_ANIMATIONS.get(EntityType.player).put("walk_w", makeSprites(0.25f, EntityType.player, "walk_w", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("walk_a", makeSprites(0.25f, EntityType.player, "walk_a", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("walk_s", makeSprites(0.25f, EntityType.player, "walk_s", true));
        TYPE_ANIMATIONS.get(EntityType.player).put("walk_d", makeSprites(0.25f, EntityType.player, "walk_d", true));

        TYPE_ANIMATIONS.get(EntityType.player).put("hit_w", makeSprites(0.25f, EntityType.player, "hit_w", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("hit_a", makeSprites(0.25f, EntityType.player, "hit_a", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("hit_s", makeSprites(0.25f, EntityType.player, "hit_s", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("hit_d", makeSprites(0.25f, EntityType.player, "hit_d", false));

        TYPE_ANIMATIONS.get(EntityType.player).put("dash_w", makeSprites(0.25f, EntityType.player, "dash_w", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("dash_a", makeSprites(0.25f, EntityType.player, "dash_a", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("dash_s", makeSprites(0.25f, EntityType.player, "dash_s", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("dash_d", makeSprites(0.25f, EntityType.player, "dash_d", false));

        TYPE_ANIMATIONS.get(EntityType.player).put("death_w", makeSprites(0.25f, EntityType.player, "death_w", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("death_a", makeSprites(0.25f, EntityType.player, "death_a", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("death_s", makeSprites(0.25f, EntityType.player, "death_s", false));
        TYPE_ANIMATIONS.get(EntityType.player).put("death_d", makeSprites(0.25f, EntityType.player, "death_d", false));


    }

    private static Animation<Sprite> makeSprites(float duration, EntityType entityType, String animation,
                                                 boolean loop) {
        Array<Sprite> array = new Array<>();

        String name = entityType.toString();
        if(animation!=null) {
            name= name + '_' + animation;
        }

        for (TextureAtlas.AtlasRegion a : Strafer.assetManager
                .get("spritesheets/" + entityType + "/" + entityType + ".atlas", TextureAtlas.class)
                .findRegions(name)) {
            a.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            Sprite s = new Sprite(a);

            s.setScale(Strafer.SCALE_FACTOR);
            array.add(s);

        }
        return new Animation<Sprite>(duration, array, getPlayMode(loop));
    }

    private static Animation<Sprite> makeSprites(float duration, EntityType entityType,
                                                 boolean loop) {
        return makeSprites(duration,entityType,null,loop);
    }
        private static Animation.PlayMode getPlayMode(boolean loop) {
        return loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL;
    }
}
