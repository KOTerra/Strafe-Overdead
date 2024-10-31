package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class ShadowComponent implements Component {
    public int elevation = 0;
    public Vector2 position = new Vector2(0, 0);
    public float radius = 0f;

}
