package com.strafergame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.input.UIControl;
import com.strafergame.ui.menus.GameOverMenu;
import com.strafergame.ui.menus.PauseMenu;
import de.golfgl.gdx.controllers.ControllerMenuStage;

public class UiManager extends ControllerMenuStage implements Disposable {

    private HUD hud;

    public UiManager(Viewport viewport, SpriteBatch spriteBatch) {
        super(viewport, spriteBatch);
    }

    public void init() {
        setSendMouseOverEvents(false);
        //((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(this);
        //Gdx.input.setInputProcessor(this);

        Skin skin = new Skin(new TextureAtlas(Gdx.files.internal(
                "ui/styles/commodore64/skin/uiskin.atlas"
        ))) {
            @Override
            protected Json getJsonLoader(FileHandle skinFile) {
                Json json = super.getJsonLoader(skinFile);
                json.setIgnoreUnknownFields(true);
                return json;
            }
        };
        skin.load(Gdx.files.internal(
                "ui/styles/commodore64/skin/uiskin.json"
        ));

        SkinMapper.map(skin);

        VisUI.load(skin);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        triggerChanges();
        controllerInput();
        highlight();
    }

    private void triggerChanges() {
        if (UIControl.PAUSE_TRIGGER) {
            GameStateType state = GameStateManager.getState();
            switch (state) {
                case PLAY: {
                    GameStateManager.changeState(GameStateType.PAUSE);
                    break;
                }
                case SETTINGS_MENU: {
                    GameStateManager.changeState(GameStateType.TITLE_MENU);
                    break;
                }
                default:
                    break;
            }
        }
        if (GameStateManager.isState(GameStateType.CUTSCENE)) {
            //emptyTrigger();//maybe change
        }
    }

    public void pauseTrigger() {
        this.addActor(PauseMenu.getInstance());
        PauseMenu.getInstance().setVisible(true);
        if (hud != null) {
            hud.setVisible(false);
        }
    }

    public void resumeTrigger() {
        PauseMenu.getInstance().setVisible(false);
        if (hud != null) {
            hud.setVisible(true);
        }
    }

    public void emptyTrigger() {
        PauseMenu.getInstance().setVisible(false);
        GameOverMenu.getInstance().setVisible(false); // Add this line

        if (hud != null) {
            hud.setVisible(false);
        }
    }

    public void gameOverTrigger() {

        // Add this line
        if (GameOverMenu.getInstance().getStage() == null) {
            this.addActor(GameOverMenu.getInstance());
        }

        GameOverMenu.getInstance().setVisible(true);

        if (getHud() != null) {
            getHud().setVisible(false);
        }
    }


    public static boolean canControlUI() {
        if (GameStateManager.isState(GameStateType.PLAY)) {
            return false;
        }
        return true;
    }

    private void controllerInput() {//nu merge clicku
        if (UIControl.DOWN_SELECT) {
            moveFocusByDirection(MoveFocusDirection.south);
        }
        if (UIControl.UP_SELECT) {
            moveFocusByDirection(MoveFocusDirection.north);
        }
        if (UIControl.LEFT_SELECT) {
            moveFocusByDirection(MoveFocusDirection.west);
        }
        if (UIControl.RIGHT_SELECT) {
            moveFocusByDirection(MoveFocusDirection.east);
        }
        if (UIControl.NEXT) {
            moveFocusByList(true);
        }
        if (UIControl.PREVIOUS) {
            moveFocusByList(false);
        }
        //aici e problema ar trb rezolvat doar cu overrideuri sa nu se strice internele din super
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {//provizoriu e ok
            fireEventOnActor(getFocusedActor(), UIControl.SELECT ? InputEvent.Type.touchDown : InputEvent.Type.touchUp, 0, null);
        }
        UIControl.UP_SELECT = false;
        UIControl.DOWN_SELECT = false;
        UIControl.LEFT_SELECT = false;
        UIControl.RIGHT_SELECT = false;
        UIControl.SELECT = false;
        UIControl.NEXT = false;
        UIControl.PREVIOUS = false;
    }


    private void highlight() {

        // Add this line
        if (getFocusedActor() == null) {
            return;
        }

        for (Actor a : getFocusableActors()) {
            Color c = a.getColor();
            c.a = .6f;
            a.setColor(c);
        }

        Color c = getFocusedActor().getColor();
        c.a = 1f;
        getFocusedActor().setColor(c);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public HUD getHud() {
        return hud;
    }

    public void setHud(HUD hud) {
        this.hud = hud;
        this.addActor(hud);
    }

}
