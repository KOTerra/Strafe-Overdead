package com.strafergame.ui.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import com.strafergame.game.ecs.system.save.Save;
import com.strafergame.game.ecs.system.save.SaveSystem;
import de.golfgl.gdx.controllers.ControllerScrollPane;

import java.util.ArrayList;
import java.util.List;

public class LoadSaveMenu extends Table {
    /// Slots horizontal, saveindexes vertical
    Save lastSave;

    List<FileHandle> savesFiles = new ArrayList<>();

    private ControllerScrollPane scrollPane;
    private VisTextButton playButton = new VisTextButton(Strafer.i18n.get("playButton"));
    private VisTextButton backButton = new VisTextButton(Strafer.i18n.get("backButton"));

    public LoadSaveMenu() {

        setFillParent(true);
        align(Align.center);
        Strafer.uiManager.addActor(this);

        scrollPane = new ControllerScrollPane(new SavesListPane());
        this.add(scrollPane);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameStateManager.changeState(GameStateType.PLAY);
            }
        });
        this.add(playButton);
        Strafer.uiManager.addFocusableActor(playButton);
        Strafer.uiManager.setFocusedActor(playButton);
        row();

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                GameStateManager.changeState(GameStateType.TITLE_MENU);
            }
        });
        add(backButton);
        Strafer.uiManager.addFocusableActor(backButton);
        row();

    }

    private class SavesListPane extends Table {
        public SavesListPane() {
            align(Align.top);
            setFillParent(true);
            defaults().space(20);

            for (FileHandle file : SaveSystem.getSavesFiles()) {
                addSaveEntry(file);
            }
        }

        void addSaveEntry(FileHandle file) {
            VisLabel label = new VisLabel(file.path());
            int[] indeces = Save.extractIndices(file.path());

            label.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getTouchFocus()) {
                        System.out.println("selected: " + label.getText());
                        SaveSystem.setCurrentSave(new Save(indeces[0], indeces[1]));
                    }
                }
            });

            label.setUserObject(file);
            this.add(label).row();
            Strafer.uiManager.addFocusableActor(label);
        }
    }


}
