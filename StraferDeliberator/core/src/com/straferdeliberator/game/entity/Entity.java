package com.straferdeliberator.game.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.straferdeliberator.game.world.GameWorld;

/**
 * A physical object that resides in the gameworld.
 * @author mihai_stoica
 */
public class Entity extends Actor {
	private Sprite sprite;
	BodyDef bodyDef;
	Body body;
	public Entity(GameWorld gw) {
		super();
		body=gw.getBox2dWorld().createBody(bodyDef);
	}
}
