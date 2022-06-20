package com.rulerofnightmares.game;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class JoinServerMenu extends FXGLMenu {

	private static final int SIZE = 150;
	
	public JoinServerMenu(){
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
        
        shape.setTranslateY(windowHeight/2+100);
        shape.setTranslateX(windowLength/2-SIZE-10);
        
        var shape2 = new Rectangle(SIZE*2, SIZE / 2);
        shape2.setStrokeWidth(2.5);
        shape2.strokeProperty().bind(
                Bindings.when(shape2.hoverProperty()).then(Color.RED).otherwise(Color.BLACK)
        );

        shape2.fillProperty().bind(
                Bindings.when(shape2.pressedProperty()).then(Color.RED).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
        );
        
        shape2.setTranslateY(windowHeight/2+200);
        shape2.setTranslateX(windowLength/2-SIZE-10);
        
        shape2.setOnMouseClicked( e-> FXGL.getSceneService().popSubScene());
        
        
        TextField input = new TextField();
        input.setScaleX(2);
        input.setScaleY(2);
        input.setTranslateX(windowLength/2-85);
        input.setTranslateY(windowHeight/2+10);
        
        shape.setOnMouseClicked( e-> {
        	System.out.println(input.getText());
        	Game.ip = (String) input.getText();
        	fireNewGame();
        });
        
        Text textJoinButton = FXGL.getUIFactoryService().newText("JOIN", Color.WHITE, FontType.GAME, 40.0);
        textJoinButton.setTranslateX(windowLength/2-50);
        textJoinButton.setTranslateY(windowHeight/2+150);
        textJoinButton.setMouseTransparent(true);
        
        Text textBackButton = FXGL.getUIFactoryService().newText("BACK", Color.WHITE, FontType.GAME, 40.0);
        textBackButton.setTranslateX(windowLength/2-50);
        textBackButton.setTranslateY(windowHeight/2+250);
        textBackButton.setMouseTransparent(true);
        
        Text textEnterAddress = FXGL.getUIFactoryService().newText("Enter the Server address", Color.WHITE, FontType.GAME, 40.0);
        textEnterAddress.setTranslateX(windowLength/2-210);
        textEnterAddress.setTranslateY(windowHeight/2-53);
        textEnterAddress.setMouseTransparent(true);
        
        getContentRoot().getChildren().addAll(shape, shape2, textEnterAddress, textJoinButton, textBackButton,input);
	}
}
