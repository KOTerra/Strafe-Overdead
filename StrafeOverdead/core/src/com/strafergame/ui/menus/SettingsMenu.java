package com.strafergame.ui.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.strafergame.Strafer;
import com.strafergame.screens.TitleScreen;

public class SettingsMenu extends Table {

	private final TabbedPane tabbedPane;

	private final Table currentContentTable;

	private final Tab graphicsTab;
	private final Tab audioTab;
	private final Tab controlsTab;

	private final VisTextButton backButton;

	private Strafer game;

	public SettingsMenu(Strafer game) {
		this.game = game;

		setFillParent(true);
		align(Align.top);
		Strafer.uiManager.addActor(this);

		backButton = new VisTextButton("<-    ");

		// Set up the tabs
		tabbedPane = new TabbedPane();
		tabbedPane.getTabsPane().setFillParent(true);
		tabbedPane.getTabsPane().setDraggable(null);
		tabbedPane.getTabsPane().addActor(backButton);
		tabbedPane.getTabsPane().fillX().align(Align.center);
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showTitleMenu();
			}
		});

		// Add the tabs to the menu
		add(tabbedPane.getTable()).expand().top().fillX().row();

		currentContentTable = new Table();
		add(currentContentTable).expand().fillX();

		graphicsTab = new Tab(false, false) {
			Table content = new Table();
			private final ScrollPane graphicsScrollPane = new ScrollPane(new GraphicsSettingsPane());

			@Override
			public String getTabTitle() {
				return "Graphics";
			}

			@Override
			public Table getContentTable() {
				content.add(graphicsScrollPane).fill().expand();
				return content;
			}

		};

		audioTab = new Tab(false, false) {
			Table content = new Table();
			private final ScrollPane audioScrollPane = new ScrollPane(new AudioSettingsPane());

			@Override
			public String getTabTitle() {
				return "Audio";
			}

			@Override
			public Table getContentTable() {
				content.add(audioScrollPane).fill().expand();
				return content;
			}
		};

		controlsTab = new Tab(false, false) {
			Table content = new Table();
			private final ScrollPane controlsScrollPane = new ScrollPane(new ControlsSettingsPane());

			@Override
			public String getTabTitle() {
				return "Controls";
			}

			@Override
			public Table getContentTable() {
				content.add(controlsScrollPane).fill().expand();
				return content;
			}
		};
		tabbedPane.addListener(new TabbedPaneAdapter() {
			@Override
			public void switchedTab(Tab tab) {
				Table content = tab.getContentTable();
				currentContentTable.clearChildren();
				currentContentTable.add(content).expand().fill();
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

		public GraphicsSettingsPane() {
			// Add the graphics settings widgets to the table
			// ...
			setFillParent(true);
			pad(150);
			defaults().space(20);
			align(Align.top);
			add(new VisLabel("graphicSetting")).row();
			add(new VisLabel("graphicSetting 2"));

		}

		public void updateSettings() {
			// Update the graphics settings based on user input
			// ...
		}
	}

	// Inner class for the audio settings pane
	private static class AudioSettingsPane extends Table {

		public AudioSettingsPane() {
			// Add the audio settings widgets to the table
			// ...
			setFillParent(true);
			pad(150);
			defaults().space(20);
			align(Align.top);
			add(new VisLabel("audioSetting")).row();
			add(new VisLabel("audioSetting 2"));
		}

		public void updateSettings() {
			// Update the audio settings based on user input
			// ...
		}
	}

	// Inner class for the controls settings pane
	private static class ControlsSettingsPane extends Table {

		public ControlsSettingsPane() {
			// Add the controls settings widgets to the table
			// ...
			setFillParent(true);
			pad(150);
			defaults().space(20);
			align(Align.top);
			add(new VisLabel("controlSetting")).row();
			add(new VisLabel("controlSetting 2")).row();

			add(new VisLabel("controlSetting")).row();
			add(new VisLabel("controlSetting 2")).row();

			add(new VisLabel("controlSetting")).row();
			add(new VisLabel("controlSetting 2")).row();

			add(new VisLabel("controlSetting")).row();
			add(new VisLabel("controlSetting 2")).row();

			add(new VisLabel("controlSetting")).row();
			add(new VisLabel("controlSetting 2"));
		}

		public void updateSettings() {
			// Update the controls settings based on user input
			// ...
		}
	}

	private void showTitleMenu() {
		if (Strafer.titleScreen == null) {
			Strafer.titleScreen = new TitleScreen(game);
		}
		game.setScreen(Strafer.titleScreen);

	}
}
