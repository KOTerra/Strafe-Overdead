package com.strafergame.game.ecs.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.*;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.world.GameWorld;
import com.strafergame.game.world.collision.Box2DWorld;

public class MovementSystem extends IteratingSystem {

    private float accumulator = 0f;
    private Box2DWorld box2dWorld;

    public MovementSystem(Box2DWorld box2dWorld) {
        super(Family.all(Box2dComponent.class, PositionComponent.class, MovementComponent.class).get());
        this.box2dWorld = box2dWorld;
    }

    @Override
    public void update(float delta) {
        playerMovement();

        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        accumulator += frameTime;

        while (accumulator >= GameWorld.FIXED_TIME_STEP) {
            savePositions();

            // AI movement
            applySteering();

            accumulator -= GameWorld.FIXED_TIME_STEP;
            box2dWorld.step(GameWorld.FIXED_TIME_STEP);
        }

        float alpha = accumulator / GameWorld.FIXED_TIME_STEP;
        interpolateRenderPositions(alpha);
    }

    private void applySteering() {
        for (Entity e : this.getEntities()) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            if (b2dCmp != null && b2dCmp.initiatedPhysics) {
                SteeringComponent steerCmp = ComponentMappers.steering().get(e);
                if (steerCmp != null) {
                    steerCmp.update();
                }
            }
        }
    }

    private void playerMovement() {
        for (Entity e : this.getEntities()) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            if (b2dCmp.initiatedPhysics) {
                MovementComponent movCmp = ComponentMappers.movement().get(e);
                EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

                if (typeCmp.entityType.equals(EntityType.player)) {
                    switch (typeCmp.entityState) {
                        case idle:
                        case walk: {
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, movCmp.dir.y * movCmp.maxLinearSpeed);
                            if (movCmp.isMoving()) {
                                typeCmp.entityState = EntityState.walk;
                            } else {
                                typeCmp.entityState = EntityState.idle;
                            }
                            break;
                        }
                        case attack: {
                            // Slide logic we implemented earlier
                            Vector2 currentVel = b2dCmp.body.getLinearVelocity();
                            currentVel.scl(0.92f);
                            if (currentVel.len() < 0.2f) currentVel.set(0, 0);
                            b2dCmp.body.setLinearVelocity(currentVel);
                            break;
                        }
                        case dash: {

                            //Apply slight damping so the dash doesn't
                            Vector2 dashVel = b2dCmp.body.getLinearVelocity();
                            dashVel.scl(0.98f);
                            b2dCmp.body.setLinearVelocity(dashVel);
                            break;
                        }
                        case hit: {
                            break;
                        }
                        case jump: {
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, movCmp.maxLinearSpeed * 1.5f);
                            break;
                        }
                        case fall: {
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, -movCmp.maxLinearSpeed * 1.5f);
                            float dif = ComponentMappers.elevation().get(e).prevIncrementalY - b2dCmp.body.getPosition().y;
                            if (Math.abs(dif) >= 1) {
                                ComponentMappers.elevation().get(e).prevIncrementalY = b2dCmp.body.getPosition().y;
                                ComponentMappers.elevation().get(e).elevation -= 1;
                                ComponentMappers.position().get(e).elevation -= 1;
                            }
                            break;
                        }
                        case death: {
                            b2dCmp.body.setLinearVelocity(0f, 0f);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void savePositions() {
        for (Entity e : this.getEntities()) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            PositionComponent posCmp = ComponentMappers.position().get(e);
            posCmp.prevPos = b2dCmp.body.getPosition().cpy();
        }
    }

    private void interpolateRenderPositions(float alpha) {
        for (Entity e : this.getEntities()) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            PositionComponent posCmp = ComponentMappers.position().get(e);
            posCmp.renderPos.set(MathUtils.lerp(posCmp.prevPos.x, b2dCmp.body.getPosition().x, alpha),
                    MathUtils.lerp(posCmp.prevPos.y, b2dCmp.body.getPosition().y, alpha));
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }

}