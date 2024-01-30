package com.strafergame.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.input.UIControl;
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
        VisUI.load(SkinScale.X2);

    }


    @Override
    public void act(float delta) {
        super.act(delta);
        triggerChanges();
    }

    private void triggerChanges() {
        if (UIControl.PAUSE_TRIGGER) {
            GameStateType state = GameStateManager.getState();
            switch (state) {
                case PLAY: {
                    GameStateManager.changeState(GameStateType.PAUSE);
                    break;
                } case SETTINGS_MENU:{
                    GameStateManager.changeState(GameStateType.TITLE_MENU);
                    break;
                }
                default:
                    break;
            }
        }
        if (GameStateManager.isState(GameStateType.CUTSCENE)) {
            emptyTrigger();//maybe change
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
        if (hud != null) {
            hud.setVisible(false);
        }
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
