package com.strafergame.game.ecs.system.interaction.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.game.ecs.component.AttackComponent;
import com.strafergame.game.ecs.component.EntityTypeComponent;
import com.strafergame.game.ecs.component.ItemComponent;
import com.strafergame.game.ecs.component.StatsComponent;
import com.strafergame.game.ecs.component.physics.Box2dComponent;
import com.strafergame.game.ecs.factories.ItemEntityFactory;
import com.strafergame.game.ecs.states.EntityState;
import com.strafergame.input.PlayerControl;

public class CombatExecutor {
    public static void executeMeleeAttack(final Entity owner, final Entity meleeItem) {
        final EntityTypeComponent typeCmp = ComponentMappers.entityType().get(owner);
        final StatsComponent statsCmp = ComponentMappers.stats().get(owner);
        final Box2dComponent b2dCmp = ComponentMappers.box2d().get(owner);
        final ItemComponent itmCmp = ComponentMappers.item().get(meleeItem);
        final AttackComponent meleeAttackCmp = ComponentMappers.attack().get(meleeItem);
        final EntityEngine entityEngine = EntityEngine.getInstance();

        typeCmp.entityState = EntityState.attack;
        typeCmp.entitySubState = EntityState.AttackSubstate.melee;


        itmCmp.holdPosition = ItemEntityFactory.inferHoldPositionOnDirection(owner);

        float targetX = b2dCmp.body.getPosition().x + itmCmp.holdPosition.x;
        float targetY = b2dCmp.body.getPosition().y + itmCmp.holdPosition.y;

        meleeAttackCmp.body.setTransform(targetX, targetY, itmCmp.holdPosition.z);
        meleeAttackCmp.body.setActive(true);

        if (!entityEngine.getEntities().contains(meleeItem, true)) {
            entityEngine.addEntity(meleeItem);
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                typeCmp.entityState = EntityState.idle;
                typeCmp.entitySubState = EntityState.NoneSubstate.none;
                meleeAttackCmp.body.setActive(false);
            }
        }, statsCmp.meleeAttackDuration);
    }

    public static void executeRangedAttack(final Entity owner, final Entity projectile, Vector2 target) {
        final EntityTypeComponent ownerTypeCmp = ComponentMappers.entityType().get(owner);
        final StatsComponent ownerStatsCmp = ComponentMappers.stats().get(owner);
        final Box2dComponent ownerB2dCmp = ComponentMappers.box2d().get(owner);
        final ItemComponent itmCmp = ComponentMappers.item().get(projectile);
        final AttackComponent rangedAttackCmp = ComponentMappers.attack().get(projectile);
        final EntityEngine entityEngine = EntityEngine.getInstance();

        ownerTypeCmp.entityState = EntityState.attack;
        ownerTypeCmp.entitySubState = EntityState.AttackSubstate.shoot;


        Vector2 playerPos = ownerB2dCmp.body.getPosition();
        Vector3 camPos = Strafer.worldCamera.position;

        // ------------------------------------------------------------
        // 2. Dynamic Distance Calculation
        // ------------------------------------------------------------
        // We use Gdx.input.getX()/getY() to find the actual mouse distance from screen center
        float screenCenterX = Gdx.graphics.getWidth() / 2f;
        float screenCenterY = Gdx.graphics.getHeight() / 2f;

        Vector3 centerWorld = new Vector3(screenCenterX, screenCenterY, 0);
        Vector3 mouseWorld = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        Strafer.worldCamera.unproject(centerWorld);
        Strafer.worldCamera.unproject(mouseWorld);

        // This is the actual world-distance from the camera center to the cursor
        float actualWorldDistance = centerWorld.dst(mouseWorld);

        // ------------------------------------------------------------
        // 3. Create the Convergence Point
        // ------------------------------------------------------------
        // We project the target direction out from the CAMERA by the ACTUAL distance of the mouse.
        // This ensures the projectile path intersects exactly where the cursor is visually.
        float lookAtX = camPos.x + (target.x * actualWorldDistance);
        float lookAtY = camPos.y + (target.y * actualWorldDistance);

        // Calculate the direction from the PLAYER to that specific point.
        Vector2 worldDirection = new Vector2(lookAtX - playerPos.x, lookAtY - playerPos.y).nor();
        float angle = MathUtils.atan2(worldDirection.y, worldDirection.x);

        // ------------------------------------------------------------
        // 4. Spawn & Physics
        // ------------------------------------------------------------
        itmCmp.holdPosition = ItemEntityFactory.inferHoldPositionOnDirection(owner);

        float spawnOffset = 1.0f;
        float spawnX = playerPos.x + worldDirection.x * spawnOffset;
        float spawnY = playerPos.y + worldDirection.y * spawnOffset;

        rangedAttackCmp.body.setTransform(spawnX, spawnY, angle);
        rangedAttackCmp.body.setActive(true);
        rangedAttackCmp.body.setAwake(true);

        rangedAttackCmp.body.setLinearVelocity(0, 0);
        rangedAttackCmp.body.setAngularVelocity(0);

        float desiredSpeed = 15f;
        float mass = rangedAttackCmp.body.getMass();
        Vector2 impulse = new Vector2(worldDirection.x * desiredSpeed * mass, worldDirection.y * desiredSpeed * mass);

        rangedAttackCmp.body.applyLinearImpulse(impulse, rangedAttackCmp.body.getWorldCenter(), true);

        entityEngine.addEntity(projectile);

        // ------------------------------------------------------------
        // 5. Timers (Cleanup and State Reset)
        // ------------------------------------------------------------
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (entityEngine.getEntities().contains(projectile, true)) {
                    entityEngine.removeEntity(projectile);
                }
            }
        }, ownerStatsCmp.rangedAttackDeletionTime);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ownerTypeCmp.entityState = EntityState.idle;
                ownerTypeCmp.entitySubState = EntityState.NoneSubstate.none;
            }
        }, ownerStatsCmp.rangedAttackDuration);
    }

    public static void executeAreaAttack(final Entity owner, final Entity effect, float radius) {

    }
}