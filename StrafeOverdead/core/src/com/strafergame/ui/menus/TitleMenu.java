package com.strafergame.ui.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.screens.GameScreen;
import com.strafergame.screens.SettingsScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class TitleMenu extends Table {
    Strafer game;
    Group background = new Group();
    VisImage banner;

    public TitleMenu() {
        this.game = Strafer.getInstance();
        makeBackground();

        setFillParent(true);
        pad(150);
        defaults().space(20);
        align(Align.right);
        Strafer.uiManager.addActor(this);
        setTouchable(Touchable.enabled);
        makeButtons();
    }

    private void makeBackground() {
        banner = new VisImage(Strafer.assetManager.get("ui/textures/banner.png", Texture.class));
        background.addActor(banner);
        banner.setAlign(Align.center);
        Strafer.uiManager.addActor(background);

    }

    private void makeButtons() {
        VisTextButton loadGameButton = new VisTextButton(Strafer.i18n.get("playButton"));

        row();
        add(loadGameButton);
        Strafer.uiManager.addFocusableActor(loadGameButton);
        Strafer.uiManager.setFocusedActor(loadGameButton);
        Strafer.uiManager.setEscapeActor(loadGameButton);

        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showLoadGameMenu();
            }
        });


        VisTextButton optionsButton = new VisTextButton(Strafer.i18n.get("optionsButton"));
        row();
        add(optionsButton);
        Strafer.uiManager.addFocusableActor(optionsButton);
        optionsButton.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(SettingsScreen.getInstance());
            }
        });


        VisTextButton quitButton = new VisTextButton(Strafer.i18n.get("quitButton"));
        row();
        add(quitButton);
        Strafer.uiManager.addFocusableActor(quitButton);
        quitButton.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void setVisible(boolean a) {
        if (a) {
            background.addAction(Actions.sequence(alpha(0), delay(0.1f), fadeIn(0.6f, Interpolation.fade)));
            this.addAction(Actions.sequence(alpha(0), delay(0.1f), fadeIn(0.6f, Interpolation.fade)));
        }
        super.setVisible(a);
        background.setVisible(a);

    }

    public void resize() {
        background.setBounds(0, 0, Strafer.uiManager.getWidth(), Strafer.uiManager.getHeight());
        banner.setScale(background.getHeight() / banner.getHeight());

    }

    private void showLoadGameMenu() {
        game.setScreen(GameScreen.getInstance());
    }

}
