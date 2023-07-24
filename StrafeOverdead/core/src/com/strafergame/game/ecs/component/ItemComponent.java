package com.strafergame.game.ecs.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class ItemComponent implements Component {
    public Entity owner;
    public Vector2 holdPosition=new Vector2(0,0);
}
