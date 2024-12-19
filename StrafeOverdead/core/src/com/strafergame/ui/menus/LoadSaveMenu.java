package com.strafergame.ui.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
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
    SavesListPane savesListPane;
    private final ControllerScrollPane scrollPane;
    private final VisTextButton playButton = new VisTextButton(Strafer.i18n.get("playButton"));
    private final VisTextButton backButton = new VisTextButton(Strafer.i18n.get("backButton"));
    List<SaveEntry> entries = new ArrayList<>();

    public LoadSaveMenu() {
        setFillParent(true);
        align(Align.top);

        savesListPane = new SavesListPane();
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

    public void updateEntries() {
        for (SaveEntry entry : entries) {
            entry.update();
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
            SaveEntry entry = new SaveEntry(file);


            int[] indices = Save.extractIndices(file.path());
            entry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getTouchFocus()) {
                        System.out.println("Selected: " + entry.label.getText());
                        SaveSystem.setCurrentSave(new Save(indices[0], indices[1]));
                        GameStateManager.changeState(GameStateType.PLAY);
                    }
                }
            });

            entries.add(entry);
            add(entry).expandX().fillX().row();

            Strafer.uiManager.addFocusableActor(entry.label);
            if (first == null) {
                first = entry.label;
            }
        }

    }

    private class SaveEntry extends Table {
        FileHandle file;
        VisLabel label;
        Stack imageStack;
        VisImage saveImage;

        public SaveEntry(FileHandle file) {
            this.file = file;


            imageStack = new Stack();


            //VisImage playerImage=new VisImage((Texture) Strafer.assetManager.get("images/player_static.png"));

            add(imageStack).size(240, 135).pad(10); // Adjust size and padding

            label = new VisLabel(Save.getSaveFileInfo(file.path()).toString());
            add(label).expandX().fillX();
        }

        public void update() {
            FileHandle screenshot = Gdx.files.external(file.pathWithoutExtension() + ".png");
            Texture screenshotTexture;
            if (!screenshot.exists()) {
                screenshotTexture = Strafer.assetManager.get("ui/textures/banner.png");
            } else {
                screenshotTexture = new Texture(screenshot);
            }

            saveImage = new VisImage(screenshotTexture);
            imageStack.add(saveImage);
            label.setText(Save.getSaveFileInfo(file.path()).toString());
        }
    }
}
