package com.strafergame.assets;

import com.badlogic.gdx.utils.Array;
import com.strafergame.game.ecs.states.EntityType;

/**
 * a class that specifies what kinds of animations are attributed to an entity how the can be loaded and provided
 */
public abstract class AnimationDictionary {

    public static void loadEntries(){

    }
    private class AnimationDictionarEntry {
        EntityType type;
    }
}
