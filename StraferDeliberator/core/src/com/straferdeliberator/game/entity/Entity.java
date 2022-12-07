package com.straferdeliberator.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A physical object that resides in the gameworld.
 * 
 * @author mihai_stoica
 */
public class Entity extends Actor {
	protected Sprite sprite;
	Animation<TextureRegion> animation;// regions taken from TextureAtlas
	// maybe add animations themselves in asset manager or make them static fields

	BodyDef bodyDef;
	Body body;

	public Entity() {

	}

	@Override
	public void act(float delta) {

	}

	public Sprite getSprite() {
		return sprite;
	}

}
