package com.rulerofnightmares.game;

import java.awt.Insets;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
    public class MainMenu extends FXGLMenu {

        private static final int SIZE = 150;

        private Animation<?> animation;

        public MainMenu() {
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
            
            Text textNewGame = FXGL.getUIFactoryService().newText("New Game", Color.WHITE, FontType.GAME, 24.0);
            textNewGame.setTranslateX(windowLength/2-40);
            textNewGame.setTranslateY(windowHeight/2-53);
            textNewGame.setMouseTransparent(true);
            
            Text textOptions = FXGL.getUIFactoryService().newText("Options", Color.WHITE, FontType.GAME, 24.0);
            textOptions.setTranslateX(windowLength/2-40);
            textOptions.setTranslateY(windowHeight/2+43);
            textOptions.setMouseTransparent(true);

            Text textExit = FXGL.getUIFactoryService().newText("Exit", Color.WHITE, FontType.GAME, 24.0);
            textExit.setTranslateX(windowLength/2-40);
            textExit.setTranslateY(windowHeight/2+140);
            textExit.setMouseTransparent(true);
            
            shape.setOnMouseClicked(e -> FXGL.getSceneService().pushSubScene(new NewGameMenu()));
            shape3.setOnMouseClicked( e-> FXGL.getGameController().exit());

            getContentRoot().getChildren().addAll(shape, shape2, shape3, textNewGame, textExit, textOptions);

            getContentRoot().setScaleX(0);
            getContentRoot().setScaleY(0);

            animation = FXGL.animationBuilder()
                    .duration(Duration.seconds(0.66))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .scale(getContentRoot())
                    .from(new Point2D(0, 0))
                    .to(new Point2D(1, 1))
                    .build();
        }

        @Override
        public void onCreate() {
            animation.setOnFinished(EmptyRunnable.INSTANCE);
            animation.stop();
            animation.start();
        }

        @Override
        protected void onUpdate(double tpf) {
            animation.onUpdate(tpf);
        }
    }