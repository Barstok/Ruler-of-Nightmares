package com.rulerofnightmares.game;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.ui.FontType;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class NewGameMenu extends FXGLMenu{
	
	private static final int SIZE = 150;
	
	public NewGameMenu() {
		super(MenuType.MAIN_MENU);
		
		var windowLength = FXGL.getAppWidth();
        var windowHeight = FXGL.getAppHeight();
		
		var bg = new Rectangle(windowLength,windowHeight);
        Image img = new Image("https://i.ibb.co/2YDSJTf/mainmenu-theme.png");
        bg.setFill(new ImagePattern(img));
        
        getContentRoot().getChildren().add(bg);
        
        var shape = new Rectangle(SIZE*2, SIZE / 2);
        shape.setStrokeWidth(2.5);
        shape.strokeProperty().bind(
                Bindings.when(shape.hoverProperty()).then(Color.RED).otherwise(Color.BLACK)
        );

        shape.fillProperty().bind(
                Bindings.when(shape.pressedProperty()).then(Color.RED).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
        );
        
        shape.setTranslateY(windowHeight/2-100);
        shape.setTranslateX(windowLength/2-SIZE);
        
        var shape2 = new Rectangle(SIZE*2, SIZE / 2);
        shape2.setStrokeWidth(2.5);
        shape2.strokeProperty().bind(
                Bindings.when(shape2.hoverProperty()).then(Color.RED).otherwise(Color.BLACK)
        );

        shape2.fillProperty().bind(
                Bindings.when(shape2.pressedProperty()).then(Color.RED).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
        );
        
        shape2.setTranslateY(windowHeight/2);
        shape2.setTranslateX(windowLength/2-SIZE);

        var shape3 = new Rectangle(SIZE*2, SIZE / 2);
        shape3.setStrokeWidth(2.5);
        shape3.strokeProperty().bind(
                Bindings.when(shape3.hoverProperty()).then(Color.RED).otherwise(Color.BLACK)
        );

        shape3.fillProperty().bind(
                Bindings.when(shape3.pressedProperty()).then(Color.RED).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
        );

        shape3.setTranslateY(windowHeight/2+100);
        shape3.setTranslateX(windowLength/2-SIZE);
        
        Text textNewServer = FXGL.getUIFactoryService().newText("Create Server", Color.WHITE, FontType.GAME, 24.0);
        textNewServer.setTranslateX(windowLength/2-40);
        textNewServer.setTranslateY(windowHeight/2-53);
        textNewServer.setMouseTransparent(true);
        
        Text textJoin = FXGL.getUIFactoryService().newText("Join Server", Color.WHITE, FontType.GAME, 24.0);
        textJoin.setTranslateX(windowLength/2-40);
        textJoin.setTranslateY(windowHeight/2+43);
        textJoin.setMouseTransparent(true);

        Text textBack = FXGL.getUIFactoryService().newText("Back", Color.WHITE, FontType.GAME, 24.0);
        textBack.setTranslateX(windowLength/2-40);
        textBack.setTranslateY(windowHeight/2+140);
        textBack.setMouseTransparent(true);
        
        shape.setOnMouseClicked(e -> {
        	Game.isServer = true;
        	fireNewGame();
        });
        
        shape2.setOnMouseClicked(e -> FXGL.getSceneService().pushSubScene(new JoinServerMenu()));
        
        shape3.setOnMouseClicked( e-> FXGL.getSceneService().popSubScene());
        
        getContentRoot().getChildren().addAll(shape,shape2,shape3,textNewServer,textJoin,textBack);
	}
}
