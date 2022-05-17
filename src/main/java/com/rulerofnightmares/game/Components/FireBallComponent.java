package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

public class FireBallComponent extends Component {

    public static int FIREBALL_SPEED = 3;

    private Point2D vector;


    @Override
    public void onUpdate(double tpf) {
        entity.translate(vector.getX() * FIREBALL_SPEED, vector.getY() * FIREBALL_SPEED);
    }

    @Override
    public void onAdded(){
        Entity player = FXGL.getWorldProperties().getObject("player");
        vector = new Point2D(FXGL.getInput().getMousePositionWorld().getX(), FXGL.getInput().getMousePositionWorld().getY())
                .subtract(player.getCenter()).normalize();
    }
}
