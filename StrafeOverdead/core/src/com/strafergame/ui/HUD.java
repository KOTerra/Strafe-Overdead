package com.strafergame.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.strafergame.Strafer;
import com.strafergame.input.PlayerControl;

public class HUD extends Table {

    private VisProgressBar healthBar;

    public static VisLabel debugInfo;
    public static String debugInfoText = "";
    private Entity player;

    public HUD() {
        setFillParent(true);
        pad(40);

        align(Align.center);
        Strafer.uiManager.addActor(this);
        this.healthBar = makeHealthBar();
        if (Gdx.app.getType().equals(ApplicationType.iOS) || Gdx.app.getType().equals(ApplicationType.Android)) {
            mobileUI();
        }

        debugInfo = makeDebugLabel();
    }

    private VisLabel makeDebugLabel() {
        VisLabel label = new VisLabel();
        label.setText(debugInfoText);
        label.setFontScale(.5f);
        add(label).top().right();
        return label;
    }

    private VisProgressBar makeHealthBar() {
        VisProgressBar healthbar = new VisProgressBar(0, 200, 1, false);

        add(healthbar).expandX().width(healthbar.getMaxValue()).top().left();

        row();
        add(new Table()).fillY().expandY();
        row();
        return healthbar;
    }

    private void mobileUI() {
        align(Align.bottomLeft);

        final float deadzone = 5f;
        final Touchpad touchpad = new Touchpad(deadzone, VisUI.getSkin());
        touchpad.setScale(20);
        touchpad.setOrigin(Align.center);
        touchpad.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PlayerControl.MOVE_UP = touchpad.getKnobY() > deadzone;
                PlayerControl.MOVE_LEFT = touchpad.getKnobX() < -deadzone;
                PlayerControl.MOVE_DOWN = touchpad.getKnobY() < -deadzone;
                PlayerControl.MOVE_RIGHT = touchpad.getKnobX() > deadzone;

            }
        });
        add(touchpad).bottom().left().pad(60);
        // row();
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
