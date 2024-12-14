package com.strafergame.ui.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.*;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.game.ecs.system.save.Save;
import com.strafergame.game.ecs.system.save.SaveSystem;
import com.strafergame.input.UIControl;
import com.strafergame.ui.UiManager;
import de.golfgl.gdx.controllers.ControllerScrollPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadSaveMenu extends Table {
    Save lastSave;

    private final ControllerScrollPane scrollPane;
    private final VisTextButton playButton = new VisTextButton(Strafer.i18n.get("playButton"));
    private final VisTextButton backButton = new VisTextButton(Strafer.i18n.get("backButton"));

    public LoadSaveMenu() {
        setFillParent(true);
        align(Align.top);

        SavesListPane savesListPane = new SavesListPane();
        scrollPane = new ControllerScrollPane(savesListPane);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setFadeScrollBars(false);

        add(scrollPane).expand().fill().row();

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameStateManager.changeState(GameStateType.TITLE_MENU);
            }
        });
        add(backButton).pad(10).align(Align.bottomRight);

        Strafer.uiManager.addActor(this);
        System.out.println(Strafer.uiManager.setFocusedActor(savesListPane.first));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (UIControl.BACK) {
            GameStateManager.changeState(GameStateType.TITLE_MENU);
        }
    }

    private class SavesListPane extends Table {
        VisLabel first;

        public SavesListPane() {
            align(Align.top);
            defaults().space(20);

            for (FileHandle file : SaveSystem.getSavesFiles()) {
                addSaveEntry(file);
            }

        }

        void addSaveEntry(FileHandle file) {
            Table entry = new Table();

            Texture texture = new Texture(Gdx.files.external(file.pathWithoutExtension() + ".png"));

            float originalWidth = texture.getWidth();
            float originalHeight = texture.getHeight();
            float aspectRatio = originalWidth / originalHeight;

            float maxWidth = 250;
            float maxHeight = 150;
            float displayWidth, displayHeight;

            if (aspectRatio > 1) { // Wider than tall
                displayWidth = maxWidth;
                displayHeight = maxWidth / aspectRatio;
            } else { // Taller than wide or square
                displayHeight = maxHeight;
                displayWidth = maxHeight * aspectRatio;
            }

            // Add image
            VisImage saveImage = new VisImage(texture);
            entry.add(saveImage).size(displayWidth, displayHeight).pad(10); // Adjust size and padding

            // Add label
            VisLabel label = new VisLabel(file.path());
            entry.add(label).expandX().fillX(); // Expand label to fill remaining space

            // Handle click event
            int[] indices = Save.extractIndices(file.path());
            entry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getTouchFocus()) {
                        System.out.println("Selected: " + label.getText());
                        SaveSystem.setCurrentSave(new Save(indices[0], indices[1]));
                        GameStateManager.changeState(GameStateType.PLAY);
                    }
                }
            });

            add(entry).expandX().fillX().row();

            Strafer.uiManager.addFocusableActor(label);
            if (first == null) {
                first = label;
            }
        }

    }
}
