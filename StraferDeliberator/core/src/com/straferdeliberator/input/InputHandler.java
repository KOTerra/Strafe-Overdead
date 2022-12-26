package com.straferdeliberator.input;

public interface InputHandler {

	/**
	 * sets the values of playerController
	 * 
	 * @param playerController
	 */
	void process(PlayerController playerController);

	void process(UIController uiController);

}
