package com.straferdeliberator.game.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.straferdeliberator.Strafer;
import com.straferdeliberator.game.entity.player.Player;
import com.straferdeliberator.game.world.collision.Box2DWorld;

public class GameWorld extends Stage implements Disposable {

	private Sprite background;
	private Player playerTest;

	Box2DWorld box2DWorld = new Box2DWorld();

	public GameWorld() {
		super(Strafer.extendViewport, Strafer.spriteBatch);

		addTestAssets();
	}

	@Override
	public void act(float delta) {
		Strafer.updateStateTime(delta);

		box2DWorld.step(delta);

		for (Actor a : this.getActors()) {
			a.act(delta);
		}
	}

	@Override
	public void draw() {

		background.draw(Strafer.spriteBatch);

		for (Actor a : this.getActors()) {
			a.draw(getBatch(), 1);
		}

	}

	@Override
	public void dispose() {
		box2DWorld.dispose();
	}

	void addTestAssets() {
		BodyDef body = new BodyDef();
		body.type = BodyType.DynamicBody;

		background = new Sprite(Strafer.assetManager.get("images/back.png", Texture.class));

		background.setPosition(0, 0);
		background.setSize(background.getWidth() * Strafer.SCALE_FACTOR, background.getHeight() * Strafer.SCALE_FACTOR);

		playerTest = new Player();
		this.addActor(playerTest);
	}

	public Box2DWorld getBox2DWorld() {
		return box2DWorld;
	}

}
