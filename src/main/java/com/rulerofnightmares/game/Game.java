package com.rulerofnightmares.game;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

public class Game extends GameApplication {

	@Override
	protected void initSettings(GameSettings settings) {

		//pod klawiszem "1" menu, w którym można zaznaczać hitboxy itp.
		settings.setDeveloperMenuEnabled(true);
		settings.setTitle("Ruler of Nightmares");
		settings.setWidth(800);
		settings.setHeight(600);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
