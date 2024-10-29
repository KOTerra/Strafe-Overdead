package com.strafergame.game.ecs.component.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.strafergame.Strafer;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShadowComponent implements Component {
    int elevation = 0;
    Vector2 position = new Vector2();
    float radius = 0f;
    //shapedrawer for all shadows elipse, modify position and Z filtering in elevation system, draw in rendering system or shadow system?
}
