package com.strafergame.game.ecs.system.render;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.ecs.component.physics.PositionComponent;
import com.strafergame.game.ecs.component.SpriteComponent;
import com.strafergame.game.ecs.component.world.ShadowComponent;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class RenderingSystem extends SortedIteratingSystem {

    private SpriteBatch batch; // a reference to our spritebatch
    private ShapeDrawer shadowDrawer;
    private TextureRegion onePixelTextureRegion;
    // String vertexShader;
    // String fragmentShader;
    // ShaderProgram shaderProgram;

    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of
    // each other
    private Comparator<Entity> comparator = new ZComparator();

    private ComponentMapper<SpriteComponent> spriteMapper;
    private ComponentMapper<PositionComponent> positionMapper;


    public RenderingSystem() {
        super(Family.all(SpriteComponent.class, PositionComponent.class).get(), new ZComparator());

        this.batch = Strafer.spriteBatch;
        initShadowDrawer();
        // vertexShader = Gdx.files.internal("shaders/default.vert").readString();
        // fragmentShader = Gdx.files.internal("shaders/default.frag").readString();
        // shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

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

        batch.enableBlending();
        batch.begin();
        // batch.setShader(shaderProgram);

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            SpriteComponent spriteCmp = spriteMapper.get(entity);
            PositionComponent posCmp = positionMapper.get(entity);
            if (posCmp.isMapLayer) {
                Strafer.tiledMapRenderer.renderTileLayer(ComponentMappers.mapLayer().get(entity).layer);
            }
            if (spriteCmp.sprite == null || posCmp.isHidden) {
                continue;
            }

            ShadowComponent shdCmp = ComponentMappers.shadow().get(entity);

            if (shdCmp != null) {
                shadowDrawer.filledEllipse(shdCmp.position.x, shdCmp.position.y, shdCmp.radius*.05f, shdCmp.radius * .025f);
            }

            batch.draw(spriteCmp.sprite, posCmp.renderPos.x - spriteCmp.width / 2, posCmp.renderPos.y, // -
                    // getHeight()
                    // / 2, //
                    // coordonatele
                    spriteCmp.width / 2, 0, // pct in care e rotit,centru
                    spriteCmp.width, spriteCmp.height, // width/height
                    1, 1, // scale
                    spriteCmp.sprite.getRotation()); // rotation

        }

        batch.end();
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
       // shadowDrawer.setColor(Color.CYAN);
    }

}