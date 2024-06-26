package com.strafergame.game.world.collision;

import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.strafergame.Strafer;

public class Box2DWorld implements Disposable {
    private final World world;

    private final Box2DDebugRenderer debugRenderer;


    public Box2DWorld() {
        world = new World(new Vector2(0f, 0f), false);
        debugRenderer = new Box2DDebugRenderer();
        world.setContactListener(new FilteredContactListener());
        world.setContactFilter(new ElevationContactFilter());
    }

    public void step(float delta) {
        world.step(delta, 6, 2);
        world.clearForces();
    }

    public void render() {
        if (Strafer.inDebug) {
            debugRenderer.render(world, Strafer.worldCamera.combined);
        }
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }

    public World getWorld() {
        return world;
    }
}
