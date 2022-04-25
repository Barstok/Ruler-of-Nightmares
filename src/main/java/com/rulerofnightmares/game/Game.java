package com.rulerofnightmares.game;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;

public class Game extends GameApplication {

	private Entity player;

	@Override
	protected void initSettings(GameSettings settings) {

		//pod klawiszem "1" menu, w którym można zaznaczać hitboxy itp.
		settings.setDeveloperMenuEnabled(true);
		settings.setTitle("Ruler of Nightmares");
		settings.setWidth(800);
		settings.setHeight(600);
	}

	@Override
	protected void initGame(){

		getGameWorld().addEntityFactory(new EntitiesFactory());

		player = spawn("Player",100,100);

	}

	public static void main(String[] args) {
		launch(args);
	}

}
