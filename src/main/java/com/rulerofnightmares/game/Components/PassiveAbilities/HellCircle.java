package com.rulerofnightmares.game.Components.PassiveAbilities;

import java.util.ArrayList;
import java.util.List;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CircleShapeData;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.rulerofnightmares.game.EntityType;
import com.rulerofnightmares.game.Components.DamageDealerComponent;

import javafx.geometry.Point2D;

public class HellCircle extends Component {

    public static final int RADIUS = 30;
    public static final int MAX_FLAMES = 6;
    
    private int rotationPoint = 0;

    //zaleznie od poziomu umiejetnosci
    private byte rotationVelocity = 1;
    private int flamesCount = MAX_FLAMES;
    private int dmg = 5;

    //ehhh
    private static final double FLAME_CENTER = 12.5;

    private List<Entity> flames = new ArrayList<Entity>();

    @Override
    public void onAdded() {
        var position = entity.getCenter();
        for(int x=0 ; x<MAX_FLAMES ; x++){
            flames.add( new EntityBuilder()
                .type(EntityType.BULLET)
                .view("flame.png")
                .at(new Point2D(position.getX()+FXGLMath.cosDeg(360/MAX_FLAMES*x)*RADIUS-FLAME_CENTER,
                position.getY()+FXGLMath.sinDeg(360/MAX_FLAMES*x)*RADIUS-FLAME_CENTER))
                //hitbox bedzie do poprawki po zmianie wyglądu
                .bbox( new HitBox(new Point2D(2, 2), new CircleShapeData(10)))
                .collidable()
                .with( new DamageDealerComponent(dmg))
                .buildAndAttach());
        }
    }

    @Override
    public void onUpdate(double tpf) {
        var position = entity.getCenter();

        for(int x=0 ; x<MAX_FLAMES ; x++){
            flames.get(x).setPosition(new Point2D(position.getX()+FXGLMath.cosDeg(360/MAX_FLAMES*x+rotationPoint)*RADIUS-FLAME_CENTER,
                position.getY()+FXGLMath.sinDeg(360/MAX_FLAMES*x+rotationPoint)*RADIUS-FLAME_CENTER));
        }
        rotationPoint += rotationVelocity;
        if(rotationPoint >= 360) rotationPoint = 0;
    }

}
