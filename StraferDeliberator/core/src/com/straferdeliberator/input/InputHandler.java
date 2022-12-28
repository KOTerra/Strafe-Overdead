package com.straferdeliberator.input;

public interface InputHandler {

	/**
	 * sets the values of playerControl
	 * 
	 * @param playerController
	 */
	void process(PlayerControl playerControl);

	void process(UIControl uiControl);

}
