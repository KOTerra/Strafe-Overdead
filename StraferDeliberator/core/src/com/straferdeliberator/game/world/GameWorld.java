package com.straferdeliberator.game.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameWorld {
	World box2dWorld;
	Stage stage;
	
	public GameWorld() {
		this.box2dWorld=new World(new Vector2(0,9.81f), true);
		this.stage=new Stage();
	}
	
	public World getBox2dWorld() {
		return box2dWorld;
	}

	public Stage getStage() {
		return stage;
	}
	
	
}
