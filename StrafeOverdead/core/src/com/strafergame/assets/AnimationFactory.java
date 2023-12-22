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

        for (EntityType e : EntityType.values()) {
            if (Strafer.assetManager.contains("spritesheets/" + e + "/" + e + ".atlas")) {
                TextureAtlas ta = Strafer.assetManager
                        .get("spritesheets/" + e + "/" + e + ".atlas", TextureAtlas.class);

                String reg = "^" + e.toString() + "_";
                for (TextureAtlas.AtlasRegion ar : ta.getRegions()) {
                    String s = ar.toString();
                    s = s.replaceFirst(reg, "");
                    //AnimationDictionary stuff or other descriptors in name_0.25_loop_etc
                    TYPE_ANIMATIONS.get(e).put(s, makeSprites(0.25f, e, s, true));
                }
            }
        }
    }

    private static Animation<Sprite> makeSprites(float duration, EntityType entityType, String animation, boolean loop) {
        Array<Sprite> array = new Array<>();

        String name = entityType.toString();
        if (animation != null) {
            name = name + '_' + animation;
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
        return makeSprites(duration, entityType, null, loop);
    }

    private static Animation.PlayMode getPlayMode(boolean loop) {
        return loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL;
    }
}
