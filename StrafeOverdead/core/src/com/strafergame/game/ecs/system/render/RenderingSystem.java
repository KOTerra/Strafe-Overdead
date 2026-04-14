package com.strafergame.game.ecs.system.render;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.ai.SteeringComponent;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.world.ShadowComponent;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderingSystem extends SortedIteratingSystem {

    private SpriteBatch batch; // a reference to our spritebatch
    private ShapeDrawer shadowDrawer;
    private TextureRegion onePixelTextureRegion;
    String vertexShader;
    String fragmentShader;
    ShaderProgram shaderProgram;

    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private Comparator<Entity> comparator = new ZComparator();

    private ComponentMapper<SpriteComponent> spriteMapper;
    private ComponentMapper<PositionComponent> positionMapper;

    public final Array<Integer> renderedElevations = new Array<>();

    public RenderingSystem() {
        super(Family.all(SpriteComponent.class, PositionComponent.class).get(), new ZComparator());

        this.batch = Strafer.spriteBatch;
        initShadowDrawer();
        vertexShader = Gdx.files.internal("shaders/default.vert").readString();
        fragmentShader = Gdx.files.internal("shaders/default.frag").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shaderProgram.getLog());
        }

        spriteMapper = ComponentMappers.sprite();
        positionMapper = ComponentMappers.position();

        renderQueue = new Array<>();
    }

    public Entity getNearest() {
        return renderQueue.first();
    }

    @Override
    public void update(float deltaTime) {   //for player xray, render its sprite again with low opacity last in the queue, over anything else
        super.update(deltaTime);

        renderQueue.sort(comparator);

        renderedElevations.clear();

        int lastElevation = Integer.MIN_VALUE;

        for (Entity entity : renderQueue) {
            PositionComponent posCmp = positionMapper.get(entity);

            if (posCmp.elevation != lastElevation) {
                renderedElevations.add(posCmp.elevation);
                lastElevation = posCmp.elevation;
            }
        }
    }

    public void renderElevation(int elevation) {

        //  transparency is discarded (for Stencil) and colors are multiplied (for Shadows).
        batch.setShader(shaderProgram);
        batch.enableBlending();
        batch.begin();

        for (Entity entity : renderQueue) {

            SpriteComponent spriteCmp = spriteMapper.get(entity);
            PositionComponent posCmp = positionMapper.get(entity);

            if (posCmp.elevation != elevation) {
                continue;
            }

            if (posCmp.isMapLayer) {
                TiledMapTileLayer layer = ComponentMappers.mapLayer().get(entity).layer;
                if (layer.isVisible()) {
                    Strafer.tiledMapRenderer.renderTileLayer(layer);
                }
            }

            if (spriteCmp.sprite == null || posCmp.isHidden) {
                continue;
            }

            ShadowComponent shdCmp = ComponentMappers.shadow().get(entity);

            if (shdCmp != null) {
                shadowDrawer.filledEllipse(shdCmp.position.x, shdCmp.position.y,
                        shdCmp.radius * .05f,
                        shdCmp.radius * .025f);
            }

            //  Reset batch color to White before drawing the sprite to not tint the sprites
            batch.setColor(Color.WHITE);

            batch.draw(spriteCmp.sprite,
                    posCmp.renderPos.x - spriteCmp.width / 2,
                    posCmp.renderPos.y,
                    spriteCmp.width / 2,
                    0,
                    spriteCmp.width,
                    spriteCmp.height,
                    1,
                    1,
                    spriteCmp.sprite.getRotation());

            if (Strafer.inDebug) {
                SteeringComponent steerCmp = ComponentMappers.steering().get(entity);
                if (steerCmp != null && steerCmp.debugPath != null && steerCmp.debugPath.size > 1) {
                    shadowDrawer.setColor(new Color(0, 1, 0, 0.5f));
                    for (int i = 0; i < steerCmp.debugPath.size - 1; i++) {
                        Vector2 current = steerCmp.debugPath.get(i);
                        Vector2 next = steerCmp.debugPath.get(i + 1);
                        shadowDrawer.line(current.x, current.y, next.x, next.y, 0.1f);
                    }
                }
            }
        }

        batch.end();
        batch.setShader(null); // Return to default shader for UI or other rendering
    }

    public void clearQueue() {
        renderQueue.clear();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    void initShadowDrawer() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawPixel(0, 0);
        Texture pixelTexture = new Texture(pixmap);
        pixmap.dispose();
        onePixelTextureRegion = new TextureRegion(pixelTexture, 0, 0, 1, 1);
        shadowDrawer = new ShapeDrawer(batch, onePixelTextureRegion);

        shadowDrawer.setColor(new Color(0, 0, 0, .25f));
    }
}