package com.strafergame.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.strafergame.Strafer;
import com.strafergame.ui.menus.TitleMenu;

public class TitleScreen implements Screen {

    private static TitleScreen instance;
    TitleMenu titleMenu;
    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Cursor cursor;
    float initialHeight;
    float initialWidth;

    public TitleScreen() {
        titleMenu = new TitleMenu();
        initialHeight = Gdx.graphics.getHeight();
        initialWidth = Gdx.graphics.getWidth();
    }

    @Override
    public void resize(int width, int height) {
        Strafer.uiManager.getViewport().update(width, height, true);

        titleMenu.resize();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderBackground();
        Strafer.uiManager.act(delta);
        Strafer.uiManager.draw();
        Strafer.uiManager.setDebugAll(Strafer.inDebug);

    }

    @Override
    public void dispose() {
        Strafer.uiManager.dispose();
        cursor.dispose();
        shapeRenderer.dispose();
    }

    /**
     * changed to another screen
     */
    @Override
    public void hide() {
        titleMenu.setVisible(false);
    }

    /**
     * app out of focus or closed
     */
    @Override
    public void pause() {

    }

    /**
     * app returned to focus
     */
    @Override
    public void resume() {

    }

    /**
     * changed to this screen
     */
    @Override
    public void show() {
        if (Gdx.app.getType().equals(ApplicationType.Desktop)) {

            Pixmap pixmap = new Pixmap(Gdx.files.internal("ui/cursor.png"));
            // Set hotspot (0,0 would be the top-left corner)
            cursor = Gdx.graphics.newCursor(pixmap, 0, 0);
            pixmap.dispose();
            Gdx.graphics.setCursor(cursor);
        }
        titleMenu.setVisible(true);

    }

    public void renderBackground() {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("#4a496b"));
        shapeRenderer.rect(0, initialHeight / 3f, initialWidth, initialHeight / 3f);
        shapeRenderer.end();
    }

    public static TitleScreen getInstance() {
        if (instance == null) {
            instance = new TitleScreen();
        }
        return instance;
    }
}
