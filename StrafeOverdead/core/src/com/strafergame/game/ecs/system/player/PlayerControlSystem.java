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

        // Normalize  so diagonal walking isn't faster
        if (!movCmp.dir.isZero()) {
            movCmp.dir.nor();
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
        final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        // block dashing in the air
        if (typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.fall)) {
            return;
        }

        final MovementComponent movCmp = ComponentMappers.movement().get(e);

        if (PlayerControl.DASH) {
            if (!dashTriggered && !movCmp.isDashCooldown) {
                final StatsComponent statsCmp = ComponentMappers.stats().get(e);
                final Box2dComponent b2dCmp = ComponentMappers.box2d().get(e);

                Vector2 dashDir = new Vector2(movCmp.dir);

                // Shift was hit before WASD, movCmp.dir is (0,0) Catch here
                if (dashDir.isZero()) {
                    if (PlayerControl.MOVE_UP) dashDir.y += 1;
                    if (PlayerControl.MOVE_DOWN) dashDir.y -= 1;
                    if (PlayerControl.MOVE_LEFT) dashDir.x -= 1;
                    if (PlayerControl.MOVE_RIGHT) dashDir.x += 1;

                    // Normalize so that diagonal dashes aren't faster
                    if (!dashDir.isZero()) {
                        dashDir.nor();
                    }
                }

                if (!dashDir.isZero()) {
                    dashTriggered = true;
                    movCmp.isDashCooldown = true;
                    typeCmp.entityState = EntityState.dash;

                    Vector2 dashImpulse = dashDir.scl(movCmp.dashForce);

                    // reset velocity so walking speed isn't added
                    b2dCmp.body.setLinearVelocity(0, 0);
                    b2dCmp.body.applyLinearImpulse(dashImpulse, b2dCmp.body.getWorldCenter(), true);

                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (typeCmp.entityState.equals(EntityState.dash)) {
                                typeCmp.entityState = EntityState.idle;
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
            }
        } else {
            dashTriggered = false;
        }
    }

    private void meleeAttack(Entity e) {
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        // already attacking
        if (typeCmp.entityState.equals(EntityState.attack) || typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.fall)) {
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

    private void shootAttack(Entity e) {//TODO maybe allow shooting to higher elevation +-1 lvl?
        EntityTypeComponent typeCmp = ComponentMappers.entityType().get(e);

        if (typeCmp.entityState.equals(EntityState.attack) || typeCmp.entityState.equals(EntityState.jump) || typeCmp.entityState.equals(EntityState.fall)) { //should not allow in the split second idle between jump and fall
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
            ComponentMappers.position().get(e).direction = EntityDirection.fromVector2(tempDir); //for npc will target after looking

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