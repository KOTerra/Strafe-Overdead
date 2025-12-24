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
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.DetectorComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.states.EntityType;
import com.strafergame.game.ecs.system.interaction.ProximityContact;
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

    private void move() {
        for (Entity e : this.getEntities()) {
            Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            if (b2dCmp.initiatedPhysics) {
                MovementComponent movCmp = ComponentMappers.movement().get(e);
                EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

                if (!typeCmp.entityType.equals(EntityType.player)) { // NPC Logic
                    DetectorComponent dtctrCmp = ComponentMappers.detector().get(e);
                    ElevationComponent enemyElv = ComponentMappers.elevation().get(e);
                    Entity target = GameWorld.player;

                    switch (typeCmp.entityState) {
                        case idle: {
                            if (dtctrCmp != null && ProximityContact.isPlayerInProximity(dtctrCmp) && target != null) {
                                ElevationComponent targetElv = ComponentMappers.elevation().get(target);
                                if (enemyElv != null && targetElv != null && enemyElv.elevation == targetElv.elevation) {
                                    SteeringComponent steerCmp = ComponentMappers.steering().get(e);
                                    if (steerCmp != null) {
                                        steerCmp.target = target;
                                    }
                                    typeCmp.entityState = EntityState.walk;
                                }
                            }

                            break;
                        }
                        case walk: {
                            SteeringComponent steerCmp = ComponentMappers.steering().get(e);
                            boolean stopWalking = false;

                            // check if lost proximity
                            if (dtctrCmp != null && !ProximityContact.isPlayerInProximity(dtctrCmp)) {
                                stopWalking = true;
                            }

                            // check if elevations still match
                            if (!stopWalking && target != null) {
                                ElevationComponent targetElv = ComponentMappers.elevation().get(target);
                                if (enemyElv != null && targetElv != null && enemyElv.elevation != targetElv.elevation) {
                                    stopWalking = true;
                                }
                            }

                            if (stopWalking) {
                                typeCmp.entityState = EntityState.idle;
                                if (steerCmp != null) {
                                    steerCmp.target = null;
                                }
                                b2dCmp.body.setLinearVelocity(0, 0); // Stop physics immediately
                                b2dCmp.body.setAngularVelocity(0);
                            } else {
                                if (steerCmp != null) {
                                    steerCmp.update();
                                }
                            }
                            break;
                        }
                        default:
                            break;
                    }
                } else { /// Player Logic
                    switch (typeCmp.entityState) {
                        case idle:
                        case walk:
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, movCmp.dir.y * movCmp.maxLinearSpeed);
                            typeCmp.entityState = EntityState.idle;
                            break;
                        case dash:
                            dashBodyOnce(b2dCmp.body, new Vector2(movCmp.dir.x, movCmp.dir.y), movCmp, typeCmp,
                                    movCmp.isDashCooldown, movCmp.dashForce);
                            break;
                        case hit:
                            break;
                        case jump:
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, movCmp.maxLinearSpeed * 1.5f);
                            break;
                        case fall:
                            b2dCmp.body.setLinearVelocity(movCmp.dir.x * movCmp.maxLinearSpeed, -movCmp.maxLinearSpeed * 1.5f);
                            float dif = ComponentMappers.elevation().get(e).prevIncrementalY - b2dCmp.body.getPosition().y;
                            if (Math.abs(dif) >= 1) {
                                ComponentMappers.elevation().get(e).prevIncrementalY = b2dCmp.body.getPosition().y;
                                ComponentMappers.elevation().get(e).elevation -= 1;
                                ComponentMappers.position().get(e).elevation -= 1;
                            }
                            break;
                        case death:
                            break;
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

    public void dashBodyOnce(final Body body, Vector2 direction, MovementComponent movCmp,
                             final EntityTypeComponent ettCmp, boolean dashCooldown, float dashForce) {
        Vector2 impulse = direction.cpy().scl(dashForce);
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }
}