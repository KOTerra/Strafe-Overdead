package com.straferdeliberator.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.straferdeliberator.Strafer;

/**
 * A physical object that resides in the gameworld.
 * 
 * @author mihai_stoica
 */
public class Entity extends Actor {
	protected TextureRegion textureRegion;

	Animation<TextureRegion> animation;// regions taken from TextureAtlas
	// maybe add animations themselves in asset manager or make them static fields

	BodyDef bodyDef;
	Body body;

	public Entity() {

	}

	@Override
	public void act(float delta) {

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(textureRegion, getX(), getY(), // coordonatele
				textureRegion.getRegionWidth() / 2, textureRegion.getRegionHeight() / 2, // pct in care e rotit
				textureRegion.getRegionWidth() * Strafer.SCALE_FACTOR,
				textureRegion.getRegionHeight() * Strafer.SCALE_FACTOR, // width/height
				1, 1, // scale
				super.getRotation()); // rotation
	}

}
