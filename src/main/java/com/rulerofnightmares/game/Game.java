package com.rulerofnightmares.game;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.entity.Entity;
import com.rulerofnightmares.game.Components.PlayerAnimationComponent;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

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
	protected void initInput() {
		onKey(KeyCode.W, () -> player.getComponent(PlayerAnimationComponent.class).moveUp());
		
		onKey(KeyCode.S, () -> player.getComponent(PlayerAnimationComponent.class).moveDown());
		
		onKey(KeyCode.A, () -> player.getComponent(PlayerAnimationComponent.class).moveLeft());
		
		onKey(KeyCode.D, () -> player.getComponent(PlayerAnimationComponent.class).moveRight());
		
		onKey(KeyCode.SPACE, () -> player.getComponent(PlayerAnimationComponent.class).attack());
		
	}

	@Override
	protected void initGame(){

		getGameWorld().addEntityFactory(new EntitiesFactory());

		//set template background 1440x1776 
		Node node = getAssetLoader().loadTexture("template_dev_map.png");
		GameView view = new GameView(node,0);
		getGameScene().addGameView(view);

		player = spawn("Player",100,100);

		//przypisanie "kamery" do pozycji gracza
		getGameScene().getViewport().bindToEntity(player,getSettings().getActualWidth()/2
														,getSettings().getActualHeight()/2);
		//ustawienie granic kamery
		getGameScene().getViewport().setBounds(10, 10, 1430, 1766);

	}

	public static void main(String[] args) {
		launch(args);
	}

}
