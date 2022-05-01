package com.rulerofnightmares.game;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.rulerofnightmares.game.Components.MonsterAnimationComponent;
import com.rulerofnightmares.game.Components.PlayerAnimationComponent;

import javafx.geometry.Point2D;

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

	@Spawns("Monster")
	public Entity newMonster(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.ENEMY)
				//		punkt początkowy względem początku calego wycinka png || kwadrat 16x30 czyli pi razy drzwi rzeczywisty wymiar postaci
				.bbox(new HitBox(new Point2D(48,64.5),BoundingShape.box(16,30)))
				.with(new MonsterAnimationComponent())
				.with(new CollidableComponent(true))
				.build();

	}

	@Spawns("PlayerNormalAttack")
	public Entity newPlayerNormalAttack(SpawnData data) {
		return FXGL.entityBuilder(data)
				.type(EntityType.PLAYER_NORMAL_ATTACK)
				.bbox(new HitBox(new Point2D(-5.5, -7), BoundingShape.box(15, 17)))
				.with(new CollidableComponent(true))
				.build();
	}
}
