package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.Box2dComponent;
import com.strafergame.game.ecs.component.MovementComponent;
import com.strafergame.game.ecs.component.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DHelper;
import com.strafergame.game.world.collision.Box2DWorld;

public class MovementSystem extends IteratingSystem {

	private float accumulator = 0f;

	private Box2DWorld box2dWorld;

	public MovementSystem(Box2DWorld box2dWorld) {
		super(Family.all(Box2dComponent.class, PositionComponent.class, MovementComponent.class).get());
		this.box2dWorld = box2dWorld;
	}

	private void initPhysics(Entity e) {
		Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
		PositionComponent posCmp = ComponentMappers.position().get(e);
		SpriteComponent spriteCmp = ComponentMappers.sprite().get(e);

		posCmp.prevX = -spriteCmp.width / 2;
		posCmp.prevY = -spriteCmp.height / 2;

		b2dCmp.body = Box2DHelper.createBody(box2dWorld.getWorld(), spriteCmp.width, spriteCmp.width, 0, 0,
				new Vector3(posCmp.prevX, posCmp.prevY, 0), BodyType.DynamicBody);

	}

	private void move() {
		for (Entity e : getEntities()) {
			Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
			MovementComponent movCmp = ComponentMappers.movement().get(e);
			if (!b2dCmp.initiatedPhysics) {
				initPhysics(e);
				b2dCmp.initiatedPhysics = true;
			}
			b2dCmp.body.setLinearVelocity(movCmp.dirX * movCmp.speed, movCmp.dirY * movCmp.speed);
		}
	}

	public void savePositions() {
		for (Entity e : this.getEntities()) {
			Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
			PositionComponent posCmp = ComponentMappers.position().get(e);
			posCmp.prevX = b2dCmp.body.getPosition().x;
			posCmp.prevY = b2dCmp.body.getPosition().y;
		}
	}

	private void interpolateRenderPositions(float alpha) {
		for (Entity e : this.getEntities()) {
			Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
			PositionComponent posCmp = ComponentMappers.position().get(e);
			posCmp.renderX = MathUtils.lerp(posCmp.prevX, b2dCmp.body.getPosition().x, alpha);
			posCmp.renderY = MathUtils.lerp(posCmp.prevY, b2dCmp.body.getPosition().y, alpha);
		}
	}

	@Override
	public void update(float delta) {
		move();
		float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
		accumulator += frameTime;
		while (accumulator >= GameWorld.FIXED_TIME_STEP) {
			savePositions();
			accumulator -= GameWorld.FIXED_TIME_STEP;
			box2dWorld.step(GameWorld.FIXED_TIME_STEP);
		}
		float alpha = accumulator / GameWorld.FIXED_TIME_STEP;
		interpolateRenderPositions(alpha);

	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {

	}

}
