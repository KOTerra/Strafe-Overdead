package com.strafergame.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.strafergame.Strafer;
import com.strafergame.game.GameStateManager;
import com.strafergame.game.GameStateType;
import de.golfgl.gdx.controllers.ControllerScrollPane;

public class SettingsMenu extends Table {

    private final TabbedPane tabbedPane;

    private final Table currentContentTable;

    private final Tab graphicsTab;
    private final Tab audioTab;
    private final Tab controlsTab;

    private final VisTextButton backButton;


    Skin skin = VisUI.getSkin();

    public SettingsMenu() {

        setFillParent(true);
        align(Align.top);
        Strafer.uiManager.addActor(this);

        backButton = new VisTextButton("<-    ");

        // Set up the tabs
        tabbedPane = new TabbedPane();
        tabbedPane.getTabsPane().setDraggable(null);
        tabbedPane.getTabsPane().addActor(backButton);
        tabbedPane.getTabsPane().align(Align.center).fill();
        tabbedPane.getTabsPane().clipBegin();
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameStateManager.changeState(GameStateType.TITLE_MENU);
            }
        });

        // Add the tabs to the menu
        add(tabbedPane.getTable()).top().growX().padBottom(20).row();
        currentContentTable = new Table();
        add(currentContentTable).growX();

        graphicsTab = new Tab(false, false) {
            Table content = new Table();
            private final ControllerScrollPane graphicsScrollPane = new ControllerScrollPane(new GraphicsSettingsPane());

            @Override
            public String getTabTitle() {
                return "Graphics";
            }

            @Override
            public Table getContentTable() {
                content.clear();

                content.add(graphicsScrollPane).top().grow();
                graphicsScrollPane.setScrollY(0);
                Strafer.uiManager.setFocusedActor(GraphicsSettingsPane.first);
                return content;
            }

        };

        audioTab = new Tab(false, false) {
            Table content = new Table();
            private final ControllerScrollPane audioScrollPane = new ControllerScrollPane(new AudioSettingsPane(), skin);

            @Override
            public String getTabTitle() {
                return "Audio";
            }

            @Override
            public Table getContentTable() {
                content.clear();
                content.add(audioScrollPane).growX().row();
                audioScrollPane.setScrollY(0);
                Strafer.uiManager.setFocusedActor(AudioSettingsPane.first);
                return content;
            }
        };

        controlsTab = new Tab(false, false) {
            Table content = new Table();

            private final ControllerScrollPane controlsScrollPane = new ControllerScrollPane(new ControlsSettingsPane());

            @Override
            public String getTabTitle() {
                return "Controls";
            }

            @Override
            public Table getContentTable() {
                content.clear();

                content.add(controlsScrollPane).top().expand();
                controlsScrollPane.setScrollY(0);
                Strafer.uiManager.setFocusedActor(ControlsSettingsPane.first);

                return content;
            }
        };
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                currentContentTable.clearChildren();

                currentContentTable.add(tab.getContentTable()).top().grow();


                //reset scroll to top
            }
        });

        tabbedPane.add(graphicsTab);
        tabbedPane.add(audioTab);
        tabbedPane.add(controlsTab);

    }

    public void updateSettings() {
        // Update the settings for each tab
        ((GraphicsSettingsPane) graphicsTab.getContentTable()).updateSettings();
        ((AudioSettingsPane) audioTab.getContentTable()).updateSettings();
        ((ControlsSettingsPane) controlsTab.getContentTable()).updateSettings();
    }

    // Inner class for the graphics settings pane
    private static class GraphicsSettingsPane extends Table {
        public static final VisLabel first = new VisLabel("GraphicSetting  #1");

        public GraphicsSettingsPane() {
            // Add the graphics settings widgets to the table
            // ...
            //setFillParent(true);
            // pad(150);
            defaults().space(70);


            add(first).top().row();
            Strafer.uiManager.addFocusableActor(first);
            for (int i = 2; i <= 20; i++) {
                VisLabel label = new VisLabel("GraphicSetting  #" + i);
                add(label).top().row();
                Strafer.uiManager.addFocusableActor(label);
            }


        }

        public void updateSettings() {
            // Update the graphics settings based on user input
            // ...
        }
    }

    // Inner class for the audio settings pane
    private static class AudioSettingsPane extends Table {
        public static final VisLabel first = new VisLabel("audioSetting  #1");

        public AudioSettingsPane() {
            // Add the audio settings widgets to the table
            // ...

            // pad(150);
            defaults().space(70);

            add(first).top().row();
            Strafer.uiManager.addFocusableActor(first);
            for (int i = 2; i <= 20; i++) {
                VisLabel label = new VisLabel("audioSetting  #" + i);
                add(label).top().row();
                Strafer.uiManager.addFocusableActor(label);
            }

        }

        public void updateSettings() {
            // Update the audio settings based on user input
            // ...
        }
    }

    // Inner class for the controls settings pane
    private static class ControlsSettingsPane extends Table {
        public static final VisLabel first = new VisLabel("controlSetting  #1");

        public ControlsSettingsPane() {
            // Add the controls settings widgets to the table
            // ...
            defaults().space(70);

            add(first).top().row();
            Strafer.uiManager.addFocusableActor(first);
            for (int i = 2; i <= 20; i++) {
                VisLabel label = new VisLabel("ControlSetting  #" + i);
                add(label).top().row();
                Strafer.uiManager.addFocusableActor(label);
            }

        }

        public void updateSettings() {
            // Update the controls settings based on user input
            // ...
        }
    }


}
