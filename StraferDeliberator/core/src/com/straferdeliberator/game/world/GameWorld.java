package com.straferdeliberator.game.world;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameWorld {
	Stage stage;

	public GameWorld() {
		this.stage = new Stage();
	}

	public Stage getStage() {
		return stage;
	}

}
