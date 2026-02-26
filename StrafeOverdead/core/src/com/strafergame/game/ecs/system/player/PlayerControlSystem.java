package com.strafergame.game.ecs.system.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.PlayerComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.component.physics.MovementComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.factories.ItemEntityFactory;
import com.strafergame.game.ecs.states.EntityDirection;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.game.ecs.system.interaction.EntityActionExecutor;
import com.strafergame.game.ecs.system.interaction.combat.CombatExecutor;
import com.strafergame.input.PlayerControl;
import com.strafergame.settings.KeyboardMapping;

public class PlayerControlSystem extends IteratingSystem {

    Strafer game;
    private Entity dashItem;
    private Entity meleeItem;


    private boolean jumpTriggered = false;
    private boolean dashTriggered = false;
    private boolean shootTriggered = false;
    private boolean meleeTriggered = false;

    public PlayerControlSystem() {
        super(Family.all(PlayerComponent.class).get());
        this.game = Strafer.getInstance();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //sequences
        konamiCode(entity);
        tripleAttack(entity);

        //single input
        move(entity);
        dash(entity);
        jump(entity);
        meleeAttack(entity);
        shootAttack(entity);
    }

    private void move(Entity e) {
        PositionComponent posCmp = ComponentMappers.position().get(e);
        final MovementComponent movCmp = ComponentMappers.movement().get(e);
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

        if (typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.attack) || typeCmp.entityState.equals(EntityState.dash) || typeCmp.entityState.equals(EntityState.hit) || typeCmp.entityState.equals(EntityState.death)) {
            return;
        }

        if (movCmp.isMoving()) {
            typeCmp.entityState = EntityState.walk;
        } else {
            typeCmp.entityState = EntityState.idle;
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

        if (dashItem == null) {
            dashItem = ItemEntityFactory.createMeleeItem(e, true, 3, 3);
            ComponentMappers.attack().get(dashItem).body.setActive(false);
        }

        if (PlayerControl.DASH && movCmp.isMoving() && !movCmp.isDashCooldown && !dashTriggered) {
            dashTriggered = true;
            movCmp.isDashCooldown = true;

            final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);
            final StatsComponent statsCmp = ComponentMappers.stats().get(e);
            final Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);
            final EntityEngine entityEngine = (EntityEngine) this.getEngine();

            typeCmp.entityState = EntityState.dash;

            final AttackComponent dashAttackCmp = ComponentMappers.attack().get(dashItem);
            dashAttackCmp.body.setTransform(b2dCmp.body.getPosition(), 0);
            dashAttackCmp.body.setActive(true);

            if (!entityEngine.getEntities().contains(dashItem, true)) {
                entityEngine.addEntity(dashItem);
            }

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    typeCmp.entityState = EntityState.idle;
                    dashAttackCmp.body.setActive(false);
                    if (entityEngine.getEntities().contains(dashItem, true)) {
                        entityEngine.removeEntity(dashItem);
                    }
                }
            }, movCmp.dashDuration);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    movCmp.isDashCooldown = false;
                }
            }, statsCmp.dashCooldownDuration);
        }

        if (!PlayerControl.DASH) {
            dashTriggered = false;
        }
    }


    private void meleeAttack(Entity e) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        // already attacking
        if (typeCmp.entityState.equals(EntityState.attack)) {
            return;
        }

        if (meleeItem == null) {
            meleeItem = ItemEntityFactory.createMeleeItem(e, true, 1, 2);
            // add to engine once only
            getEngine().addEntity(meleeItem);
            ComponentMappers.attack().get(meleeItem).body.setActive(false);
        }

        if (PlayerControl.ATTACK && !meleeTriggered) {
            meleeTriggered = true;
            CombatExecutor.executeMeleeAttack(e, meleeItem);
        }

        if (!PlayerControl.ATTACK) {
            meleeTriggered = false;
        }
    }


    private final Vector2 tempDir = new Vector2();

    private void shootAttack(Entity e) {
        if (ComponentMappers.entityType().get(e).entityState.equals(EntityState.attack)) {
            return;
        }

        if (PlayerControl.SHOOT && !shootTriggered) {
            shootTriggered = true;
            Entity projectile = ItemEntityFactory.createProjectile(e);
            Vector2 playerPos = ComponentMappers.box2d().get(e).body.getPosition();

            if (PlayerControl.USING_CONTROLLER) {
                if (!PlayerControl.CONTROLLER_AIM_DIRECTION.isZero()) {
                    tempDir.set(PlayerControl.CONTROLLER_AIM_DIRECTION).nor();
                } else {

                    tempDir.set(EntityDirection.toVector2(ComponentMappers.position().get(e).direction));
                    if (tempDir.x != 0) {
                        tempDir.y = 0; //shoot on one axis if not aiming
                    }
                 
                }
            } else {
                tempDir.set(PlayerControl.MOUSE_WORLD_POS.x - playerPos.x,
                        PlayerControl.MOUSE_WORLD_POS.y - playerPos.y).nor();
            }

            CombatExecutor.executeRangedAttack(e, projectile, tempDir);
        }

        if (!PlayerControl.SHOOT) {
            shootTriggered = false;
        }
    }

    private void tripleAttack(Entity entity) {
        executeActionSequence(entity, KeyboardMapping.TRIPLE_CLICK_SEQUENCE, entity1 -> {
            System.out.println("333 333 333 ");
            return true;
        });
    }

    private void konamiCode(Entity entity) {
        executeActionSequence(entity, KeyboardMapping.KONAMI_CODE_SEQUENCE, entity1 -> {
            System.out.println("EYOO");
            return true;
        });
    }


    private void executeActionSequence(Entity entity, int[] sequence, EntityActionExecutor executor) {
        boolean sequenceMatch = false;
        if (PlayerControl.actionSequence.isInTimeframe(sequence.length, PlayerControl.SEQUENCE_TIMEFRAME)) {
            Object[] seq = PlayerControl.actionSequence.getSequenceKeycodes(sequence.length).toArray();
            for (int i = 0; i < seq.length; i++) {
                sequenceMatch = (Integer) seq[i] == sequence[i];
            }
        }
        if (sequenceMatch) {
            executor.execute(entity);
        }
    }
}