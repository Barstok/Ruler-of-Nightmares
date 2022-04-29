package com.rulerofnightmares.game.Components;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.*;

import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import com.almasb.fxgl.entity.Entity;

import java.util.Random;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;

public class MonsterAnimationComponent extends Component {

    private int speed = 0;

    private int hp = 100;
    private int v_speed = 0;
    private int isAttacking = 0;

    private Array<Entity> players = new Array<Entity>(0);

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animAttack, animTakeDmg;

    public MonsterAnimationComponent() {
        animIdle = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("red_riding_hood.png"), 12, 1344 / 12, 1463 / 11, Duration.seconds(1), 1, 24);
        animAttack = new AnimationChannel(FXGL.image("red_riding_hood.png"),12,1344 / 12,1463 / 11,Duration.seconds(1),59,78);
        animTakeDmg = new AnimationChannel(FXGL.image("red_riding_hood.png"),12,1344 / 12,1463 / 11,Duration.seconds(1),120,126);

        texture = new AnimatedTexture(animIdle);
    }


    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void startAI() {
        TimerAction AI = getGameTimer().runAtInterval(() -> {
            int lock = 0;
//            for (int i = 0; i < players.size(); i++) {
//                Entity player = players.get(i);
//                if (getEntity().distance(player) < 100 && getEntity().distance(player) > 5) {
//                    if (player.getX() - entity.getX() >= 5) {
//                        this.moveRight();
//                    }
//                    else if (player.getX() - entity.getX() <= -5) {
//                        this.moveLeft();
//                    }
//                    if (player.getY() - entity.getY() >= 5) {
//                        this.moveDown();
//                    }
//                    else if (player.getY() - entity.getY() <= -5) {
//                        this.moveUp();
//                    }
//                    lock = 1;
//                }
//                else if (getEntity().distance(player) <= 5) {
//                    this.attack();
//                    lock = 1;
//                }
//            }
            if (lock == 0) {
                Random random = new Random();
                int rand = random.nextInt(4);
                if (rand == 0) {
                    this.moveUp();
                }
                else if (rand == 1) {
                    this.moveRight();
                }
                else if (rand == 2) {
                    this.moveDown();
                }
                else {
                    this.moveLeft();
                }
            }
        }, Duration.millis(1000));
        if (this.hp == 0) {
            AI.pause();
        }
    }


    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 16));
        entity.getViewComponent().addChild(texture);
//        texture.playAnimationChannel(animSpawn);
        texture.loopAnimationChannel(animIdle);
        getGameTimer().runOnceAfter(this::startAI, Duration.seconds(1));

    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf);
        entity.translateY(v_speed * tpf);
        if(isAttacking == 1) {
            texture.playAnimationChannel(animAttack);
            isAttacking = 0;
        }
        if (speed != 0 || v_speed != 0) {

            speed = (int) (speed * 0.9);
            v_speed = (int) (v_speed * 0.9);
            texture.loopAnimationChannel(animWalk);
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
