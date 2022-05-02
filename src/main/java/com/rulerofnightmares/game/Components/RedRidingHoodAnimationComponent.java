package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.*;

import com.almasb.fxgl.time.TimerAction;
import com.rulerofnightmares.game.EntityType;

import javafx.geometry.Point2D;
import javafx.util.Duration;
import com.almasb.fxgl.entity.Entity;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;

public class RedRidingHoodAnimationComponent extends Component {

    private final double RED_RIDING_HOOD_ANIMATION_DURATION = 1;

    private int speed = 0;

    private int hp = 100;
    private int v_speed = 0;
    private int isAttacking = 0;

    private boolean isAttacked;

    private TimerAction AI;

    List<Entity> players = FXGL.getGameWorld().getEntitiesByType(EntityType.PLAYER);

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animAttack, animTakeDmg;

    public RedRidingHoodAnimationComponent() {
        animIdle = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11,
                Duration.seconds(RED_RIDING_HOOD_ANIMATION_DURATION), 0, 1);
        animWalk = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11,
                Duration.seconds(RED_RIDING_HOOD_ANIMATION_DURATION), 1, 24);
        animAttack = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11,
                Duration.seconds(RED_RIDING_HOOD_ANIMATION_DURATION), 59, 78);
        //poniższa animacja nie działa, nie wiem dlaczego
        animTakeDmg = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11,
                Duration.seconds(RED_RIDING_HOOD_ANIMATION_DURATION), 120, 126);

        texture = new AnimatedTexture(animIdle);
    }

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void receiveDmgNormalAttack() {
        this.hp -= 50;
        isAttacked = true;
    }

    // pi razy drzwi działające AI, nie czuje ale rymuje, fakt faktem ten kod
    // optymalizacji potrzebuje
    public void startAI() {
        AI = getGameTimer().runAtInterval(() -> {
            for (int i = 0; i < players.size(); i++) {
                Entity player = players.get(i);
                if (player.isActive() && entity != null) {
                    if (FXGLMath.abs(getEntity().getCenter().getX() - player.getCenter().getX()) <= 10
                            && FXGLMath.abs(getEntity().getCenter().getY() - player.getCenter().getY()) <= 10) {
                        player.getComponent(PlayerAnimationComponent.class).receiveDmg(2);
                        this.attack();
                    } else if (FXGLMath.abs(getEntity().getCenter().getX() - player.getCenter().getX()) < 100
                            && FXGLMath.abs(getEntity().getCenter().getY() - player.getCenter().getY()) < 100) {
                        if (player.getCenter().getX() - entity.getCenter().getX() >= 5) {
                            this.moveRight();
                        } else if (player.getCenter().getX() - entity.getCenter().getX() <= -5) {
                            this.moveLeft();
                        }
                        if (player.getCenter().getY() - entity.getCenter().getY() >= 5) {
                            this.moveDown();
                        } else if (player.getCenter().getY() - entity.getCenter().getY() <= -5) {
                            this.moveUp();
                        }
                    }
                }
            }
        }, Duration.millis(100));
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(56, 66.5));
        entity.getViewComponent().addChild(texture);
        // texture.playAnimationChannel(animSpawn);
        texture.loopAnimationChannel(animIdle);
        getGameTimer().runOnceAfter(this::startAI, Duration.seconds(1));
        isAttacked = false;

        texture.setOnCycleFinished(() -> {
            isAttacking = 0;
        });
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf);
        entity.translateY(v_speed * tpf);
        if (this.isAttacked) {
            if (this.hp <= 0) {
//                AI.pause();
//                texture.playAnimationChannel(animWalk);
//                getGameTimer().runOnceAfter(() -> {
//                    if (entity != null) {
//                        entity.removeFromWorld();
//                    }
//                }, Duration.seconds(RED_RIDING_HOOD_ANIMATION_DURATION));
                entity.removeFromWorld();
            }
        }

        if (isAttacking == 1 && texture.getAnimationChannel() != animAttack) {
            texture.playAnimationChannel(animAttack);
        }
        if (speed != 0 || v_speed != 0) {

            if (texture.getAnimationChannel() != animWalk && isAttacking == 0)
                texture.loopAnimationChannel(animWalk);

            speed = (int) (speed * 0.9);
            v_speed = (int) (v_speed * 0.9);
        } else if (isAttacking == 0)
            texture.loopAnimationChannel(animIdle);
    }

    public void moveRight() {
        speed = 150;

        getEntity().setScaleX(-1);
    }

    public void moveLeft() {
        speed = -150;

        getEntity().setScaleX(1);
    }

    public void moveUp() {
        v_speed = -150;
    }

    public void moveDown() {
        v_speed = 150;
    }

    public void attack() {
        isAttacking = 1;
    }
}
