package com.rulerofnightmares.game;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.ui.FontType;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class EndScreen extends SubScene{
	
	private static final int SIZE = 150;
	
	public EndScreen(int scenario) {
		var windowLength = FXGL.getAppWidth();
        var windowHeight = FXGL.getAppHeight();
		
		var bg = new Rectangle(windowLength,windowHeight);
        Image img = new Image("https://i.ibb.co/2YDSJTf/mainmenu-theme.png");
        bg.setFill(new ImagePattern(img));
        
        getContentRoot().getChildren().add(bg);
        
        Text text = null;
        
        if(scenario == 1) {
        	text = FXGL.getUIFactoryService().newText("You won! Congratulations!", Color.WHITE, FontType.GAME, 40.0);
        	text.setTranslateX(windowLength/2-200);
        	text.setTranslateY(windowHeight/2-53);
        	text.setMouseTransparent(true);
        }
        else {
        	text = FXGL.getUIFactoryService().newText("You died!", Color.WHITE, FontType.GAME, 40.0);
        	text.setTranslateX(windowLength/2-80);
        	text.setTranslateY(windowHeight/2-53);
        	text.setMouseTransparent(true);
        }
        
        var shape = new Rectangle(SIZE*2, SIZE / 2);
        shape.setStrokeWidth(2.5);
        shape.strokeProperty().bind(
                Bindings.when(shape.hoverProperty()).then(Color.RED).otherwise(Color.BLACK)
        );

        shape.fillProperty().bind(
                Bindings.when(shape.pressedProperty()).then(Color.RED).otherwise(Color.color(0.1, 0.05, 0.0, 0.75))
        );
        
        shape.setTranslateY(windowHeight/2+100);
        shape.setTranslateX(windowLength/2-SIZE-20);
        
        Text textBackToMenu= FXGL.getUIFactoryService().newText("Return to menu", Color.WHITE, FontType.GAME, 24.0);
        textBackToMenu.setTranslateX(windowLength/2-80);
        textBackToMenu.setTranslateY(windowHeight/2+150);
        textBackToMenu.setMouseTransparent(true);
        
        shape.setOnMouseClicked( e-> FXGL.getGameController().gotoMainMenu());
        
        getContentRoot().getChildren().addAll(text,shape,textBackToMenu);
        
	}
}
