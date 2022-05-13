package com.rulerofnightmares.game;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CircleShapeData;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.rulerofnightmares.game.Components.DamageDealerComponent;
import com.rulerofnightmares.game.Components.FireBallComponent;
import com.rulerofnightmares.game.Components.PlayerAnimationComponent;

import com.rulerofnightmares.game.Components.RedRidingHoodAnimationComponent;
import com.rulerofnightmares.game.Components.PassiveAbilities.HellCircle;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.awt.*;

public class EntitiesFactory implements EntityFactory {
    
    @Spawns("Player")
	public Entity newPlayer(SpawnData data) {
		
		return FXGL.entityBuilder(data)
				.type(EntityType.PLAYER)
				.bbox(new HitBox(new Point2D(6,8),BoundingShape.box(16,24)))
				.with(new PlayerAnimationComponent())
				.with(new CollidableComponent(true))
				.build();
	}

	@Spawns("RedRidingHood")
	public Entity newMonster(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.ENEMY)
				//		punkt początkowy względem początku calego wycinka png || kwadrat 16x30 czyli pi razy drzwi rzeczywisty wymiar postaci
				.bbox(new HitBox(new Point2D(48,64.5),BoundingShape.box(16,30)))
				.with(new RedRidingHoodAnimationComponent())
				.with(new CollidableComponent(true))
				.build();

	}

	@Spawns("PlayerNormalAttack")
	public Entity newPlayerNormalAttack(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.PLAYER_NORMAL_ATTACK)
				.bbox(new HitBox(new Point2D(-10, -7), BoundingShape.box(25, 17)))
				.with(new CollidableComponent(true))
				.with(new DamageDealerComponent(50))
				.build();
	}

	@Spawns("FireBall")
	public Entity newFireBall(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.BULLET)
				.view("fireball.png")
				.bbox(new HitBox(new Point2D(0, 0), new CircleShapeData(10)))
				.with(new FireBallComponent())
				.with(new DamageDealerComponent(70))
				.collidable()
				.with(new ExpireCleanComponent(Duration.seconds(4)))
				.build();
	}
}
