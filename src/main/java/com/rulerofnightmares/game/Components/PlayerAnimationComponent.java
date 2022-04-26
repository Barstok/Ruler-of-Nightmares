package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.*;

import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PlayerAnimationComponent extends Component {

    private int speed = 0;
    private int v_speed = 0;
    private int isAttacking = 0;

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animAttack;

    public PlayerAnimationComponent() {
        animIdle = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 0, 3);
        animAttack = new AnimationChannel(FXGL.image("player_sprite.png"),13,32,32,Duration.seconds(0.5),27,35);

        texture = new AnimatedTexture(animIdle);
    }

  
    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 16));
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf);
        entity.translateY(v_speed * tpf);
        
        if(isAttacking == 1 && texture.getAnimationChannel() != animAttack) {
        	texture.playAnimationChannel(animAttack);
        	isAttacking = 0;
        }
        

        if (speed != 0 || v_speed != 0) {
        	
            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }

            speed = (int) (speed * 0.9);
            v_speed = (int) (v_speed * 0.9);

            if (FXGLMath.abs(speed) < 1 && FXGLMath.abs(v_speed) < 1) {
                speed = 0;
                v_speed = 0;
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void moveRight() {
        speed = 150;
        
        getEntity().setScaleX(1);
    }

    public void moveLeft() {
        speed = -150;

        getEntity().setScaleX(-1);
    }
    
    public void moveUp() {
        v_speed = -150;

        getEntity().setScaleX(1);
    }

    public void moveDown() {
        v_speed = 150;

        getEntity().setScaleX(-1);
    }
    
    public void attack() {
    	isAttacking = 1;
    }
}
