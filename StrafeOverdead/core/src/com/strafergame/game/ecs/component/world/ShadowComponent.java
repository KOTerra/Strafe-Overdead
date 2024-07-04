package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class ShadowComponent implements Component {
    int elevation = 0;
    Vector2 position = new Vector2();
    float radius = 0f;
}
