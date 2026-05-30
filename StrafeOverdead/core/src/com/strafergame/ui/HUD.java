package com.strafergame.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.ComponentMappers;
import com.strafergame.game.world.GameWorld;
import com.strafergame.input.PlayerControl;

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
        align(Align.bottomLeft);

        final float deadzone = 0.15f; // Normalized deadzone (0 to 1)
        final Touchpad touchpad = new Touchpad(deadzone, VisUI.getSkin());
        
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Set analog values
                PlayerControl.ANALOG_MOVE_X = touchpad.getKnobPercentX();
                PlayerControl.ANALOG_MOVE_Y = touchpad.getKnobPercentY();

                // Maintain digital booleans for backward compatibility/other systems
                PlayerControl.MOVE_UP = touchpad.getKnobPercentY() > 0.5f;
                PlayerControl.MOVE_LEFT = touchpad.getKnobPercentX() < -0.5f;
                PlayerControl.MOVE_DOWN = touchpad.getKnobPercentY() < -0.5f;
                PlayerControl.MOVE_RIGHT = touchpad.getKnobPercentX() > 0.5f;
            }
        });

        // Add to table with fixed size instead of using setScale (which breaks input bounds)
        add(touchpad).size(300).bottom().left().pad(60);
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
