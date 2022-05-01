package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.*;

import com.rulerofnightmares.game.EntityType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PlayerAnimationComponent extends Component {
    public static final double ATTACK_ANIMATION_DURATION = 0.5;
    private int speed = 0;
    private int v_speed = 0;
    private int isAttacking = 0;

    private boolean isAttacked;

    private int hp;

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animAttack, animAttacked, animDeath;

    public PlayerAnimationComponent() {
        //animAttacked nie działa, nie wiem czemu
        animAttacked = new AnimationChannel(FXGL.image("player_sprite.png"),13,32,32,Duration.seconds(1),78,81);
        animIdle = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 0, 3);
        animAttack = new AnimationChannel(FXGL.image("player_sprite.png"),13,32,32,Duration.seconds(ATTACK_ANIMATION_DURATION),27,35);
        animDeath = new AnimationChannel(FXGL.image("player_sprite.png"),13,32,32,Duration.seconds(1),93,97);

        texture = new AnimatedTexture(animIdle);

        texture.setOnCycleFinished( () ->{
            isAttacking = 0;
        } );
    }
    public int getHp() {
        return this.hp;
    }

    public void regenerateHP() {
        if (this.hp < 100) {
            this.hp++;
        }
    }

    public boolean isAttacking() {
        return this.isAttacking == 1;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void receiveDmg(int dmg) {
        this.isAttacked = true;
        this.hp -= dmg;
    }

  
    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 16));
        entity.getViewComponent().addChild(texture);
        this.isAttacked = false;
        this.hp = 100;
        getGameTimer().runAtInterval(this::regenerateHP, Duration.seconds(2));
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf);
        entity.translateY(v_speed * tpf);

        if (this.hp <= 0) {
            texture.playAnimationChannel(animDeath);
            getGameTimer().runOnceAfter(entity::removeFromWorld, Duration.seconds(1));
        }
        if (texture.getAnimationChannel() != animDeath) {
            if(isAttacking == 1 && texture.getAnimationChannel() != animAttack) {
                texture.playAnimationChannel(animAttack);
            }

//            if (isAttacked) {
//                //tutaj nie działa
//                texture.playAnimationChannel(animAttacked);
//                this.isAttacked = false;
//            }

            if (speed != 0 || v_speed != 0) {

                if (texture.getAnimationChannel() != animWalk&&isAttacking == 0) {
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
            else if(isAttacking == 0 ) texture.loopAnimationChannel(animIdle);
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
    }

    public void moveDown() {
        v_speed = 150;
    }
    
    public void attack() {
        if (FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER_NORMAL_ATTACK).isEmpty()) {
            isAttacking = 1;
            Entity normalAttack = spawn("PlayerNormalAttack", entity.getCenter().getX() + 10 * entity.getScaleX(), entity.getCenter().getY());
            getGameTimer().runOnceAfter(normalAttack::removeFromWorld, Duration.seconds(ATTACK_ANIMATION_DURATION));
        }
    }
}
