package com.strafergame.game.world.collision;

import com.badlogic.gdx.physics.box2d.*;

public class FilteredContactListener implements ContactListener {

    public static final short HURTBOX_CATEGORY = 0x0001;                  // 1
    public static final short DPS_HITBOX_CATEGORY = 0x0002;                   // 2
    /**
     * collision filter for a player's own sensor of proximity
     */
    public static final short PLAYER_CATEGORY = 0x0004;                    // 4
    /**
     * collision filter for a sensor that searches a player
     */
    public static final short PLAYER_DETECTOR_CATEGORY = 0x0008;           // 8


    /**
     * filter for a sensor attached to the footprint
     */
    public static final short FOOTPRINT_CATEGORY = 0x0010;                 // 16
    /**
     * filter for an elevation sensor
     */
    public static final short FOOTPRINT_DETECTOR_CATEGORY = 0x0020;        // 32

    /**
     * filter for projectiles
     */
    public static final short PROJECTILE_HITBOX_CATEGORY = 0x0040;                // 64

    public static final short SOLID_BODY_CATEGORY = 0x0080; // 128

    // --- LIGHTING BITS (Reserved Bits 8-15) ---
    public static final short LIGHT_BIT_OFFSET = 8;
    public static final short ALL_LIGHT_BITS = (short) 0xFF00;

    public static short getWallCategory(int elevation) {
        int bitShift = (elevation % 8) + LIGHT_BIT_OFFSET;
        return (short) (1 << bitShift);
    }

    /**
     * Updates fixture filters to cast shadows on a specific elevation
     * without losing existing gameplay categories (Player, Footprint, etc).
     */
    public static void setShadowFilter(Body body, int elevation) {
        if (body == null) return;
        short shadowBit = getWallCategory(elevation);
        for (Fixture fixture : body.getFixtureList()) {
            // Only solid objects (non-sensors) cast shadows
            if (!fixture.isSensor()) {
                Filter filter = fixture.getFilterData();
                filter.categoryBits &= ~ALL_LIGHT_BITS; // Clear old elevation bits
                filter.categoryBits |= shadowBit;       // Add current elevation bit //TODO also use for shooting on different elevations
                fixture.setFilterData(filter);
            }
        }
    }


    public static final float DETECTOR_RADIUS = 9;

    private final CombatContactDelegate combatDelegate = new CombatContactDelegate();
    private final VerticalityContactDelegate verticalityDelegate = new VerticalityContactDelegate();
    private final ProximityContactDelegate proximityDelegate = new ProximityContactDelegate();
    private final PhysicsBypassDelegate physicsBypassDelegate = new PhysicsBypassDelegate();

    @Override
    public void beginContact(Contact contact) {
        verticalityDelegate.beginFootprintContact(contact);
        proximityDelegate.beginProximityContact(contact);
        combatDelegate.beginAttackContact(contact);
        combatDelegate.beginProjectileWorldContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        verticalityDelegate.endFootprintContact(contact);
        proximityDelegate.endProximityContact(contact);
        combatDelegate.endAttackContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        physicsBypassDelegate.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
