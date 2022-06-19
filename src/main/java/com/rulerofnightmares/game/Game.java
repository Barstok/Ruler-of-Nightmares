package com.rulerofnightmares.game;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;
import com.rulerofnightmares.game.Components.DamageDealerComponent;
import com.rulerofnightmares.game.Components.PlayerAnimationComponent;

import com.rulerofnightmares.game.Components.RedRidingHoodAnimationComponent;
import com.rulerofnightmares.game.Components.PassiveAbilities.HellCircle;

import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import java.util.Random;

public class Game extends GameApplication {
	private Entity player;

	private final static int MAX_WAVES = 7;

	private final static int WAVES_WAIT_FACTOR = 5;

	private final static double RANDOM_BOUNDARY = 777;

	int current_wave;
	private Entity monster;

	private static final Random randomCoordinates = new Random();

	private static final int ENEMIES_PER_WAVE_FACTOR = 20;

	TimerAction wavesSpawner;

	@Override
	protected void initSettings(GameSettings settings) {

		// pod klawiszem "1" menu, w którym można zaznaczać hitboxy itp.
		settings.setDeveloperMenuEnabled(true);
		settings.setMainMenuEnabled(true);
		settings.setTitle("Ruler of Nightmares");
		settings.setWidth(1280);
		settings.setHeight(720);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("hp", 100);
		vars.put("mp", 100);
		vars.put("level", 1);
	}

	@Override
	protected void initInput() {
		onKey(KeyCode.W, () -> player.getComponent(PlayerAnimationComponent.class).moveUp());

		onKey(KeyCode.S, () -> player.getComponent(PlayerAnimationComponent.class).moveDown());

		onKey(KeyCode.A, () -> player.getComponent(PlayerAnimationComponent.class).moveLeft());

		onKey(KeyCode.D, () -> player.getComponent(PlayerAnimationComponent.class).moveRight());

		onKeyDown(KeyCode.E, () -> player.getComponent(PlayerAnimationComponent.class).shootFireBall());

		onKeyDown(KeyCode.SPACE, () -> {
			player.getComponent(PlayerAnimationComponent.class).attack();
		});

		onKeyDown(KeyCode.Q, () -> {
			player.getComponent(PlayerAnimationComponent.class).dash();
		});
	}


	@Override
	protected void initPhysics() {
		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_NORMAL_ATTACK, EntityType.ENEMY) {
			@Override
			protected void onCollisionBegin(Entity normalAttack, Entity enemy) {
				int dmg = normalAttack.getComponent(DamageDealerComponent.class).dealDmg();
				enemy.getComponent(RedRidingHoodAnimationComponent.class).receiveDmg(dmg);
				player.getComponent(PlayerAnimationComponent.class).incrementXp(20);
			}
		});

		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY, EntityType.BULLET) {
			@Override
			protected void onCollisionBegin(Entity enemy, Entity bullet){
				//troche sie syf narobil z "uniwersalnoscia", trzeba bedzie to przepisac sensowniej
				int dmg = bullet.getComponent(DamageDealerComponent.class).dealDmg();
				enemy.getComponent(RedRidingHoodAnimationComponent.class).receiveDmg(dmg);
			}
		});
	}

	@Override
	protected void initGame() {
		current_wave = 0;

		getGameWorld().addEntityFactory(new EntitiesFactory());

		// set template background 1440x1776
//		Node node = getAssetLoader().loadTexture("template_dev_map.png");
//		GameView view = new GameView(node, 0);
//		getGameScene().addGameView(view);
		
		FXGL.setLevelFromMap("mapav3.tmx");

		player = spawn("Player", 100, 100);
		FXGL.getWorldProperties().setValue("player", player);

		// przypisanie "kamery" do pozycji gracza
		getGameScene().getViewport().bindToEntity(player, getSettings().getActualWidth() / 2,
				getSettings().getActualHeight() / 2);
		// ustawienie granic kamery
		getGameScene().getViewport().setBounds(10, 10, 2400, 2400);
		wavesSpawner = getGameTimer().runAtInterval(() -> {
			FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY).forEach(Entity::removeFromWorld);
			for (int i = 0; i < ENEMIES_PER_WAVE_FACTOR * (current_wave + 1); i++) {
				spawn("RedRidingHood", randomCoordinates.nextDouble() * RANDOM_BOUNDARY, randomCoordinates.nextDouble() * RANDOM_BOUNDARY);
			}
			current_wave++;
		}, Duration.seconds(WAVES_WAIT_FACTOR));

	}

	@Override
	protected void initUI() {
		Text hpText = new Text("hp: " + player.getComponent(PlayerAnimationComponent.class).getHp().toString());
		Text mpText = new Text("mp: " + player.getComponent(PlayerAnimationComponent.class).getMp().toString());
		Text lvlText = new Text("level: " + player.getComponent(PlayerAnimationComponent.class).getCurrentLevel().toString());
		hpText.setFont(Font.font(13));
		mpText.setFont(Font.font(13));
		lvlText.setFont(Font.font(13));
		hpText.setFill(Color.BLACK);
		mpText.setFill(Color.BLACK);
		lvlText.setFill(Color.BLACK);
		hpText.textProperty().bind(getip("hp").asString("hp: %d"));
		mpText.textProperty().bind(getip("mp").asString("mp: %d"));
		lvlText.textProperty().bind(getip("level").asString("level: %d"));
		addUINode(hpText, 30, 640);
		addUINode(mpText, 30, 670);
		addUINode(lvlText, 30, 700);
	}

	@Override
	protected void onUpdate(double tpf) {
		if (current_wave == MAX_WAVES) {
			wavesSpawner.expire();
			FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY).forEach(Entity::removeFromWorld);
		}
		FXGL.getWorldProperties().setValue("hp", player.getComponent(PlayerAnimationComponent.class).getHp());
		FXGL.getWorldProperties().setValue("mp", player.getComponent(PlayerAnimationComponent.class).getMp());
		FXGL.getWorldProperties().setValue("level", player.getComponent(PlayerAnimationComponent.class).getCurrentLevel());
		if (getip("hp").getValue() <= 0) {
			showMessage("You died!", () -> {
				getGameController().gotoMainMenu();
			});
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
