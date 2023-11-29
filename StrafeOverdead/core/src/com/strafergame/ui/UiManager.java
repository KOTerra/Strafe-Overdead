package com.strafergame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.input.UIControl;
import com.strafergame.ui.menus.PauseMenu;

public class UiManager extends Stage implements Disposable {

    private HUD hud;

    public UiManager(Viewport viewport, SpriteBatch spriteBatch) {
        super(viewport, spriteBatch);

    }

    public void init() {
        ((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(Strafer.uiManager);

        VisUI.load(SkinScale.X2);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        triggerChanges();
    }

    private void triggerChanges() {
        if (UIControl.PAUSE_TRIGGER) {
            GameStateManager.getInstance().getStateMachine().changeState(GameStateType.PAUSE);
        }
        if(GameStateManager.getInstance().getStateMachine().isInState(GameStateType.CUTSCENE)){
            emptyTrigger();
        }
    }

    public void pauseTrigger() {
        this.addActor(PauseMenu.getInstance());
        PauseMenu.getInstance().setVisible(true);
        hud.setVisible(false);
    }

    public void resumeTrigger() {
        PauseMenu.getInstance().setVisible(false);
        hud.setVisible(true);
    }

    public void emptyTrigger() {
        PauseMenu.getInstance().setVisible(false);
        hud.setVisible(false);
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
