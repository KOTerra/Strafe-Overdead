package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationComponent implements Component {
    public Animation<Sprite> animation;
    public float timer = 0f;

    /**
     * Stores the animation from the previous frame to detect switches
     */
    public Animation<Sprite> prevAnimation;
}