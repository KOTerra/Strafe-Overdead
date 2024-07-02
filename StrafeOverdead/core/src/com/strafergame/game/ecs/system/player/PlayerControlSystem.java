package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.factories.ItemEntityFactory;
import com.strafergame.game.ecs.states.EntityDirection;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.input.PlayerControl;

public class PlayerControlSystem extends IteratingSystem {

    Strafer game;

    Entity item;

    private boolean jumpTriggered = false;

    public PlayerControlSystem() {
        super(Family.all(PlayerComponent.class).get());
        this.game = Strafer.getInstance();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        move(entity);
        dash(entity);
        jump(entity);
    }

    private void move(Entity e) {
        PositionComponent posCmp = ComponentMappers.position().get(e);
        final MovementComponent movCmp = ComponentMappers.movement().get(e);
        PlayerComponent plyrCmp = ComponentMappers.player().get(e);
        Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
        final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        movCmp.dir.set(0f, 0f);

        if (PlayerControl.MOVE_UP) {
            movCmp.dir.y = 1f;
            posCmp.direction = EntityDirection.w;
        }
        if (PlayerControl.MOVE_DOWN) {
            movCmp.dir.y = -1f;
            posCmp.direction = EntityDirection.s;
        }
        if (PlayerControl.MOVE_LEFT) {
            movCmp.dir.x = -1f;
            posCmp.direction = EntityDirection.a;
        }
        if (PlayerControl.MOVE_RIGHT) {
            movCmp.dir.x = 1f;
            posCmp.direction = EntityDirection.d;
        }
        if (typeCmp.entityState.equals(EntityState.jump)) {
            return;
        }
        if (movCmp.isMoving()) {
            typeCmp.entityState = EntityState.walk;
        } else {
            if (!typeCmp.entityState.equals(EntityState.death)) {
                typeCmp.entityState = EntityState.idle;
            }
        }
    }

    private void jump(Entity e) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
        if (PlayerControl.JUMP) {
            if (!jumpTriggered) {
                EntityState state = typeCmp.entityState;
                typeCmp.entityState = switch (state) {
                    case walk, run, idle -> EntityState.jump;
                    default -> state;
                };
                jumpTriggered = true;
            }
        } else {
            jumpTriggered = false;
        }
    }

    private void dash(Entity e) {
        final MovementComponent movCmp = ComponentMappers.movement().get(e);
        final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
        final PlayerComponent plyrCmp = ComponentMappers.player().get(e);

        final EntityEngine entityEngine = (EntityEngine) this.getEngine();

        if (item == null) {
            item = ItemEntityFactory.createItem(e, new Vector2(0, 0), 3, 3);
        }

        if (!movCmp.isDashCooldown) {
            if (PlayerControl.DASH && movCmp.isMoving()) {
                movCmp.isDashCooldown = true;
                typeCmp.entityState = EntityState.dash;
                if (!entityEngine.getEntities().contains(item, true)) {
                    entityEngine.addEntity(item);
                }
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        typeCmp.entityState = EntityState.idle;
                        if (entityEngine.getEntities().contains(item, true)) {
                            entityEngine.removeEntity(item);
                        }
                    }
                }, movCmp.dashDuration);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        movCmp.isDashCooldown = false;
                    }
                }, plyrCmp.dashCooldownDuration);

            }
        } else {
            typeCmp.entityState = EntityState.dash;
        }
    }


}
