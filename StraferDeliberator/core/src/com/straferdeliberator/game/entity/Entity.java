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

	protected Animation<TextureRegion> animation;// regions taken from TextureAtlas
	// maybe add animations themselves in asset manager or make them static fields

	private TextureRegion currentFrame;

	BodyDef bodyDef;
	Body body;

	public Entity() {

	}

	@Override
	public void act(float delta) {
		updateRegion(delta);
	}

	private void updateRegion(float delta) {
		currentFrame = animation.getKeyFrame(Strafer.stateTime);
		setSize(currentFrame.getRegionWidth() * Strafer.SCALE_FACTOR,
				currentFrame.getRegionHeight() * Strafer.SCALE_FACTOR);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		setScale(Strafer.SCALE_FACTOR);
		batch.draw(currentFrame, getX(), getY(), // coordonatele
				getWidth() / 2, getHeight() / 2, // pct in care e rotit
				getWidth(), getHeight(), // width/height
				1, 1, // scale
				super.getRotation()); // rotation
	}

}
