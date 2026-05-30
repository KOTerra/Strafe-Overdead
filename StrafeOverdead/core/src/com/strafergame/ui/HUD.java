package com.strafergame.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.world.GameWorld;
import com.strafergame.input.PlayerControl;
import com.strafergame.screens.CutsceneScreen;

public class HUD extends Table {

    private VisProgressBar healthBar;

    public static VisLabel debugInfo;
    public static String debugInfoText = "";
    private Entity player;

    private DialogueBox dialogueBox;

    public HUD() {
        setFillParent(true);
        pad(40);

        align(Align.center);
        Strafer.uiManager.addActor(this);
        this.healthBar = makeHealthBar();

        this.dialogueBox = new DialogueBox();
        add(dialogueBox).width(Value.percentWidth(.8f, this)).bottom().pad(20);
        dialogueBox.setVisible(false);

        if (Gdx.app.getType().equals(ApplicationType.iOS) || Gdx.app.getType().equals(ApplicationType.Android)) {
            mobileUI();
        }

        debugInfo = makeDebugLabel();
    }

    public DialogueBox getDialogueBox() {
        return dialogueBox;
    }

    private VisLabel makeDebugLabel() {
        VisLabel label = new VisLabel();
        label.setText(debugInfoText);
        label.setFontScale(.5f);
        add(label).top().right();
        return label;
    }

    private VisProgressBar makeHealthBar() {
        float max = ComponentMappers.stats().get(GameWorld.player).maxHealth;
        VisProgressBar healthbar = new VisProgressBar(0, max, .01f, false);

        add(healthbar).expandX().width(healthbar.getMaxValue() + 1).top().left();

        row();
        add(new Table()).fillY().expandY();
        row();
        return healthbar;
    }

    private void mobileUI() {
        // Create an overlay table for mobile controls to ensure they are on top and correctly positioned
        Table mobileControls = new Table();
        mobileControls.setFillParent(true);
        this.addActor(mobileControls);

        // Movement Touchpad on the left
        final float deadzone = 0.15f;
        final Touchpad touchpad = new Touchpad(deadzone, VisUI.getSkin());
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Set analog values
                PlayerControl.ANALOG_MOVE_X = touchpad.getKnobPercentX();
                PlayerControl.ANALOG_MOVE_Y = touchpad.getKnobPercentY();

                // Maintain digital booleans for backward compatibility
                PlayerControl.MOVE_UP = touchpad.getKnobPercentY() > 0.5f;
                PlayerControl.MOVE_LEFT = touchpad.getKnobPercentX() < -0.5f;
                PlayerControl.MOVE_DOWN = touchpad.getKnobPercentY() < -0.5f;
                PlayerControl.MOVE_RIGHT = touchpad.getKnobPercentX() > 0.5f;
            }
        });
        mobileControls.add(touchpad).size(300).bottom().left().expand().pad(60);

        // Action Buttons on the right
        Table buttonTable = new Table();

        // Dash Button
        VisTextButton dashButton = new VisTextButton("Dash");
        dashButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.DASH = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.DASH = false;
            }
        });
        buttonTable.add(dashButton).size(180, 120).pad(10);

        // Jump Button
        VisTextButton jumpButton = new VisTextButton("Jump");
        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.JUMP = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.JUMP = false;
            }
        });
        buttonTable.add(jumpButton).size(180, 120).pad(10).row();

        // Attack Button
        VisTextButton attackButton = new VisTextButton("Attack");
        attackButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.ATTACK = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayerControl.ATTACK = false;
            }
        });
        buttonTable.add(attackButton).size(180, 120).pad(10);

        // Cutscene Debug Button
        VisTextButton cutsceneButton = new VisTextButton("Cutscene");
        cutsceneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CutsceneScreen.playCutscene("cutscenes/yee.webm", 9.017f);
            }
        });
        buttonTable.add(cutsceneButton).size(180, 120).pad(10);

        mobileControls.add(buttonTable).bottom().right().expandY().pad(60);
    }

    public void resize() {
        setBounds(0, 0, Strafer.uiManager.getWidth(), Strafer.uiManager.getHeight());

    }

    public void hide() {
        setVisible(!isVisible());
    }

    public VisProgressBar getHealthBar() {
        return healthBar;
    }

}
