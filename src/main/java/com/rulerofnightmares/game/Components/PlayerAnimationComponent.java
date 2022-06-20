package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CircleShapeData;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.*;

import com.almasb.fxgl.time.TimerAction;
import com.rulerofnightmares.game.Components.PassiveAbilities.HellCircle;
import com.rulerofnightmares.game.EntityType;
import com.rulerofnightmares.game.Game;

import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getService;
import static com.almasb.fxgl.dsl.FXGL.showMessage;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class PlayerAnimationComponent extends Component {

    private final static double DASH_TRANSLATE = 150;

    private double dashDuration = 0.25;

    private static int dashMultiplierCeiling = 4;

    private static int HELL_CIRCLE_RADIUS = 30;

    private static final int MAX_FLAMES = 6;

    private static int hellCircleRotationPoint = 0;

    //zaleznie od poziomu umiejetnosci
    private static int HELL_CIRCLE_ROTATION_VELOCITY = 1;
    private static int HELL_CIRCLE_DMG = 5;

    //ehhh
    private static final double FLAME_CENTER = 12.5;

    private List<Entity> flames = new ArrayList<Entity>();
    
    private static int SCALE_FACTOR = 2;

    private static int HP_INCREMENT = 1;

    private static int MP_INCREMENT = 5;

    private static final int STATISTICS_INCREMENT = 30;

    private static int maxHP = 100;

    private static int maxMP = 100;

    public static final Map<Integer, Integer> LEVELS_EXP_MAP = Map.of(1, 100, 2, 200, 3, 300, 4, 400, 5, 500, 6, 600);
    public static double ATTACK_ANIMATION_DURATION = 0.5;
    private int speed = 0;
    private int v_speed = 0;
    private int isAttacking = 0;

    private boolean isAttacked;

    private Integer hp;

    private int xp;

    private static boolean hellCircleAddLock;

    private static boolean isTransformed;

    private Integer mp;

    private Integer currentLevel;

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animWalk, animAttack;

    private int dashMultiplier;

    public Integer getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public void regenerateMp() {
        if (this.mp >= maxMP) return;
        if (this.mp + MP_INCREMENT >= maxMP) this.mp = maxMP;
        else { 
        	this.mp += MP_INCREMENT;
        	if( Game.players.size() >=1 && this.entity == Game.players.get(0)) Game.myMp = this.mp;
    	}
    }

    public PlayerAnimationComponent() {
        //animAttacked nie działa, nie wiem czemu
        animIdle = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(FXGL.image("player_sprite.png"), 13, 32, 32, Duration.seconds(1), 0, 3);
        animAttack = new AnimationChannel(FXGL.image("player_sprite.png"),13,32,32,Duration.seconds(ATTACK_ANIMATION_DURATION),27,35);
        hellCircleAddLock = false;
        texture = new AnimatedTexture(animIdle);

        texture.setOnCycleFinished( () ->{
            isAttacking = 0;
        } );
    }
    public Integer getHp() {
        return this.hp;
    }

    public Integer getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentLevel(int lvl) {
        this.currentLevel = lvl;
    }

    public void incrementCurrentLevel() {
    	System.out.println("LEVEL UP!");
        this.currentLevel++;
        FXGL.set("level", this.currentLevel);
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void incrementXp(int additionalXp) {
        this.xp += additionalXp;
    }

    public void regenerateHP() {
        if (this.hp >= maxHP) return;
        if (this.hp + HP_INCREMENT >= maxHP) this.hp = maxHP;
        else {
        	this.hp += HP_INCREMENT;
        	if( Game.players.size() >=1 && this.entity == Game.players.get(0)) Game.myHp = this.hp;
        }
    }

    private void addHellCircle() {
        if (hellCircleAddLock) return;
        if (currentLevel >= 5) {
        	Entity player = this.getEntity();
            for(int x=0 ; x < MAX_FLAMES ; x++){
                flames.add( new EntityBuilder()
                        .type(EntityType.BULLET)
                        .view("flame.png")
                        .at(new Point2D(player.getX()+FXGLMath.cosDeg(360/MAX_FLAMES*x)*HELL_CIRCLE_RADIUS-FLAME_CENTER,
                                player.getY()+FXGLMath.sinDeg(360/MAX_FLAMES*x)*HELL_CIRCLE_RADIUS-FLAME_CENTER))
                        //hitbox bedzie do poprawki po zmianie wyglądu
                        .bbox( new HitBox(new Point2D(2, 2), new CircleShapeData(10)))
                        .collidable()
                        .with( new DamageDealerComponent(HELL_CIRCLE_DMG))
                        .with(new NetworkComponent())
                        .buildAndAttach());
            }
            hellCircleAddLock = true;
        }
    }

    private void rotateHellCircle() {
        var position = entity.getCenter();

        for(int x=0 ; x<MAX_FLAMES ; x++){
            flames.get(x).setPosition(new Point2D(position.getX()+FXGLMath.cosDeg(360/MAX_FLAMES*x+hellCircleRotationPoint)*HELL_CIRCLE_RADIUS-FLAME_CENTER,
                    position.getY()+FXGLMath.sinDeg(360/MAX_FLAMES*x+hellCircleRotationPoint)*HELL_CIRCLE_RADIUS-FLAME_CENTER));
        }
        hellCircleRotationPoint += HELL_CIRCLE_ROTATION_VELOCITY;
        if(hellCircleRotationPoint >= 360) hellCircleRotationPoint = 0;
    }

    public boolean canAscend() {
        if (currentLevel == 7) return false;
        return this.xp >= LEVELS_EXP_MAP.get(currentLevel);
    }

    public void transformation() {
        var position = entity.getCenter();
        FireBallComponent.FIREBALL_SPEED = 7;
        ATTACK_ANIMATION_DURATION = 0.25;
        dashDuration = 0.5;
        entity.getBoundingBoxComponent().clearHitBoxes();
        entity.getBoundingBoxComponent().addHitBox(new HitBox(new Point2D(83, 69), BoundingShape.box(26, 54)));
        entity.getTransformComponent().setScaleOrigin(new Point2D(96, 69));
        animIdle = new AnimationChannel(FXGL.image("Idle.png"), 8, 1600/8, 200, Duration.seconds(1), 0, 7);
        animWalk = new AnimationChannel(FXGL.image("Run.png"), 8, 1600/8, 200, Duration.seconds(1), 0, 7);
        animAttack = new AnimationChannel(FXGL.image("Attack1.png"),6,1200/6,200,Duration.seconds(ATTACK_ANIMATION_DURATION),0,5);
        HELL_CIRCLE_RADIUS = 77;
        HELL_CIRCLE_ROTATION_VELOCITY = 3;
        HELL_CIRCLE_DMG = 20;
        dashMultiplierCeiling = 7;
    }

    public void ascend() {
        int tempXp = LEVELS_EXP_MAP.get(currentLevel);//xp potrzebne do awansowania
        incrementCurrentLevel();
        setXp(this.xp - tempXp);
        maxMP += STATISTICS_INCREMENT;
        maxHP += STATISTICS_INCREMENT;
        MP_INCREMENT += 5;
        HP_INCREMENT++;
        addHellCircle();
        if (this.currentLevel >= 5 && !isTransformed) {
            transformation();
            isTransformed = true;
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
        entity.setScaleUniform(SCALE_FACTOR);
        this.isAttacked = false;
        isTransformed = false;
        this.hp = 100;
        this.xp = 0;
        this.mp = 0;
        this.currentLevel = 1;
        this.dashMultiplier = 1;
        getGameTimer().runAtInterval(this::regenerateHP, Duration.seconds(1));
        getGameTimer().runAtInterval(this::regenerateMp, Duration.seconds(1));
        addHellCircle();
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(speed * tpf * dashMultiplier);
        entity.translateY(v_speed * tpf * dashMultiplier);

        if (this.hp <= 0) {
        	Game.deaths++;
        	if(Game.isServer == true) {
        		System.out.println(Game.players.size());
                if(Game.deaths == Game.server.getConnections().size()) {
                	if(Game.myPlayer != null) {
                		FXGL.runOnce(() -> showMessage("You won!", () -> {
                			getGameController().gotoMainMenu();
                			}), Duration.seconds(1));
                	}
                	var data = new Bundle("");
                    data.put("won","1");
                    Game.server.broadcast(data);
                }
            }
            entity.removeFromWorld();
        }

        if (hellCircleAddLock) {
            rotateHellCircle();
        }

        if (canAscend()) ascend();

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

    public void moveRight() {
        speed = 150;
        
        getEntity().setScaleX(SCALE_FACTOR);
    }

    public void moveLeft() {
        speed = -150;

        getEntity().setScaleX(-SCALE_FACTOR);
    }

    public void shootFireBall() {
        //zakomentuj ifa, żeby spojrzeć jak to wygląda
        if (this.currentLevel < 1 || mp < 50) return;
        spawn("FireBall", entity.getCenter());
        this.mp -= 50;
        if(Game.myConnNum==0) Game.myMp = this.mp;
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
            Entity normalAttack;
            if (isTransformed) {
                normalAttack = spawn("TransformedPlayerNormalAttack", entity.getCenter().getX() + 50 * entity.getScaleX(), entity.getCenter().getY());
            }
            else {
                normalAttack = spawn("PlayerNormalAttack", entity.getCenter().getX() + 10 * entity.getScaleX(), entity.getCenter().getY());
            }
            getGameTimer().runOnceAfter(normalAttack::removeFromWorld, Duration.seconds(ATTACK_ANIMATION_DURATION));
        }
    }

    public void dash() {
        //zakomentuj ifa by sprawdzić działanie
        if (mp < 20 || currentLevel < 1) return;
        this.mp -= 20;
        if(Game.myConnNum==0) Game.myMp = this.mp;
        dashMultiplier = dashMultiplierCeiling;
        getGameTimer().runOnceAfter(() -> {
            dashMultiplier = 1;
        }, Duration.seconds(dashDuration));
    }
}

