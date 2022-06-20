package com.rulerofnightmares.game;

import static com.almasb.fxgl.dsl.FXGL.*;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.MessageHandler;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game extends GameApplication {

	private final static int MAX_WAVES = 7;

	private final static int WAVES_WAIT_FACTOR = 5;

	private final static double RANDOM_BOUNDARY = 777;

	int current_wave;
	private Entity monster;
	
	private Input clientInput;

	private static final Random randomCoordinates = new Random();

	private static final int ENEMIES_PER_WAVE_FACTOR = 20;
	
	private int playersConnected = 0;
	
	public static boolean isServer;
	
	public static String ip;
	
	private List<Entity> players = new ArrayList<Entity>();
	
	private List<Input> clientInputs = new ArrayList<Input>();
	
	private Connection<Bundle> clientConn;
	public static List<Connection<Bundle>> connections = new ArrayList<Connection<Bundle>>();
	private int myConnNum = -1;
	
	public static int myHp = 100;
	private int myHpComp = 100;
	private Rectangle hpRectangle;
	private Rectangle mpRectangle;
	
	private Entity myPlayer;
	
	TimerAction wavesSpawner;

	@Override
	protected void initSettings(GameSettings settings) {

		// pod klawiszem "1" menu, w którym można zaznaczać hitboxy itp.
		settings.setDeveloperMenuEnabled(true);
		settings.setMainMenuEnabled(true);
		
		settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                //return new SimpleGameMenu();
                return new MainMenu();
            }
        });
		
		settings.addEngineService(MultiplayerService.class);
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

	protected void initInput() {
		if(isServer) {
			onKey(KeyCode.W, () -> players.get(0).getComponent(PlayerAnimationComponent.class).moveUp());

			onKey(KeyCode.S, () -> players.get(0).getComponent(PlayerAnimationComponent.class).moveDown());

			onKey(KeyCode.A, () -> players.get(0).getComponent(PlayerAnimationComponent.class).moveLeft());

			onKey(KeyCode.D, () -> players.get(0).getComponent(PlayerAnimationComponent.class).moveRight());

			onKeyDown(KeyCode.E, () -> players.get(0).getComponent(PlayerAnimationComponent.class).shootFireBall());

			onKeyDown(KeyCode.SPACE, () -> {
				players.get(0).getComponent(PlayerAnimationComponent.class).attack();
			});

			onKeyDown(KeyCode.Q, () -> {
				players.get(0).getComponent(PlayerAnimationComponent.class).dash();
			});
		}
	}
	
	void initClientInput(int connectionNum) {
		clientInputs.add( new Input());
		
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.W)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).moveUp());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.S)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).moveDown());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.A)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).moveLeft());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.D)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).moveRight());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.E)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).shootFireBall());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.SPACE)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).attack());
		onKeyBuilder(clientInputs.get(playersConnected-1),KeyCode.Q)
				.onAction(() -> players.get(connectionNum).getComponent(PlayerAnimationComponent.class).dash());
	}



	protected void initPhysics() {
		getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_NORMAL_ATTACK, EntityType.ENEMY) {
			@Override
			protected void onCollisionBegin(Entity normalAttack, Entity enemy) {
				int dmg = normalAttack.getComponent(DamageDealerComponent.class).dealDmg();
				enemy.getComponent(RedRidingHoodAnimationComponent.class).receiveDmg(dmg);
				//players.getComponent(PlayerAnimationComponent.class).incrementXp(20);
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
		runOnce(() -> {
                getGameWorld().addEntityFactory(new EntitiesFactory());
                if (isServer) {
                	myConnNum = 0;
                    var server = getNetService().newTCPServer(55555);
                    server.setOnConnected(conn -> {
                        connections.add(conn);
                        playersConnected++;
                        
                        var data = new Bundle("");
                        data.put("myConnNum", playersConnected);

                        conn.send(data);
                                                          
//                        if(playersConnected == 1) {
//                        	getExecutor().startAsyncFX(() -> ServerSide());
//                        }
                        getExecutor().startAsyncFX(() -> playerJoined());
                    });
                    getExecutor().startAsyncFX(() -> ServerSide());
                    server.startAsync();

                } else {
                    var client = getNetService().newTCPClient(ip, 55555);
                    client.setOnConnected(conn -> {
                        clientConn = conn;
                        
                        conn.addMessageHandlerFX((connn, message) -> {
                        	if(myConnNum == -1) {
                        		var xd = message.get("myConnNum");
                        		if(xd != null) myConnNum = (int) xd;	
                        	}
                        	var xd = message.get("myHp");
                    		if(xd != null) {
                    		myHp = (int) xd;
                    		System.out.println(xd);
                        }});

                        getExecutor().startAsyncFX(() -> ClientSide());
                        
                    });
                    client.connectAsync();
                }
        }, Duration.seconds(0.5));
	}
	
	void UIinit() {
		hpRectangle = new Rectangle(FXGL.getWorldProperties().getInt("hp"), 25);
		hpRectangle.setFill(Color.RED);
		mpRectangle = new Rectangle(FXGL.getWorldProperties().getInt("mp"), 25);
		mpRectangle.setFill(Color.BLUE);
		Text lvlText = new Text("level: " + getip("level").asString());
		lvlText.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 17));
		lvlText.setFill(Color.BLACK);
		lvlText.textProperty().bind(getip("level").asString("level: %d"));
		addUINode(hpRectangle, 30, 610);
		addUINode(mpRectangle, 30, 640);
		addUINode(lvlText, 30, 700);
	}
	
	void ClientSide() {
		initInput();
		
		FXGL.setLevelFromMap("mapav3.tmx");
		
   		getService(MultiplayerService.class).addEntityReplicationReceiver(clientConn, getGameWorld());
   		
   		
   		UIinit();
   		runOnce(() ->{
   			ClientBindViewport();
   			System.out.println(myConnNum);
   			myPlayer = getGameWorld().getEntitiesByType(EntityType.PLAYER).get(myConnNum);
   		},Duration.seconds(2));
   		
   		getService(MultiplayerService.class).addInputReplicationSender(clientConn, getInput());
	}
	
	void ClientBindViewport() {
		var playerToFollow = getGameWorld().getEntitiesByType(EntityType.PLAYER);
		
		getGameScene().getViewport().bindToEntity(playerToFollow.get(playerToFollow.size()-1), getSettings().getActualWidth() / 2,
   				getSettings().getActualHeight() / 2);

   		getGameScene().getViewport().setBounds(10, 10, 1430, 1766);
	}
	
	void playerJoined(){
		var conn = connections.get(connections.size()-1);
		for(var entity: getGameWorld().getEntitiesByType(EntityType.PLAYER)) {
			getService(MultiplayerService.class).spawn(conn, entity, "Player");
		}
		var temp = spawn("Player",100+100*playersConnected,100+100*playersConnected);
		players.add(temp);
		for(var conns : connections) {
			getService(MultiplayerService.class).spawn(conns, temp, "Player");
		}
		initClientInput(playersConnected);
		getService(MultiplayerService.class).addInputReplicationReceiver(conn, clientInputs.get(playersConnected-1));
	}
	
	void ServerSide() {
		initInput();
		current_wave = 0;
        
		FXGL.setLevelFromMap("mapav3.tmx");
   		
   		initPhysics();

   		var temp = spawn("Player",100,100);
   		players.add(temp);
   		myPlayer=temp;
   		//getService(MultiplayerService.class).spawn(connections.get(0), temp, "Player");
   		
   		UIinit();
		        
        // przypisanie "kamery" do pozycji gracza
   		getGameScene().getViewport().bindToEntity(players.get(0), getSettings().getActualWidth() / 2,
   				getSettings().getActualHeight() / 2);
   		// ustawienie granic kamery
   		getGameScene().getViewport().setBounds(10, 10, 1430, 1766);
   		
   		var nmy = spawn("RedRidingHood", randomCoordinates.nextDouble() * RANDOM_BOUNDARY, randomCoordinates.nextDouble() * RANDOM_BOUNDARY);	
		for(var conn: connections) {
			getService(MultiplayerService.class).spawn(conn, nmy, "RedRidingHood");
		}
   		
		
//		wavesSpawner = getGameTimer().runAtInterval(() -> {
//			FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY).forEach(Entity::removeFromWorld);
//			for (int i = 0; i < ENEMIES_PER_WAVE_FACTOR * (current_wave + 1); i++) {
//				var nmy = spawn("RedRidingHood", randomCoordinates.nextDouble() * RANDOM_BOUNDARY, randomCoordinates.nextDouble() * RANDOM_BOUNDARY);
//				getService(MultiplayerService.class).spawn(connection, nmy, "RedRidingHood");
//			}
//			current_wave++;
//		}, Duration.seconds(WAVES_WAIT_FACTOR));
	}

	@Override
	protected void onUpdate(double tpf) {
		for(var input : clientInputs) {
			input.update(tpf);
		}
		if(hpRectangle != null && mpRectangle != null && myPlayer != null) {
			hpRectangle.setWidth(myHp);
			if(myHp!=myHpComp) {
				myHpComp = myHp;
				FXGL.getGameScene().getViewport().shakeTranslational(3);
			}
		}
	}
	
//	@Override
//	protected void onUpdate(double tpf) {
//		if (current_wave == MAX_WAVES) {
//			wavesSpawner.expire();
//			FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY).forEach(Entity::removeFromWorld);
//		}
//		FXGL.getWorldProperties().setValue("hp", player.getComponent(PlayerAnimationComponent.class).getHp());
//		FXGL.getWorldProperties().setValue("mp", player.getComponent(PlayerAnimationComponent.class).getMp());
//		FXGL.getWorldProperties().setValue("level", player.getComponent(PlayerAnimationComponent.class).getCurrentLevel());
//		if (getip("hp").getValue() <= 0) {
//			showMessage("You died!", () -> {
//				getGameController().gotoMainMenu();
//			});
//		}
//	}

	public static void main(String[] args) {
		launch(args);
	}

}
