package com.strafergame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.screens.*;

//pause and add ecs systems and listeners based on the gamestate
public enum GameStateType implements State<GameStateManager> {
    /**
     * Called on Launcher? Maybe not ok to manage states before game creation
     * <p>
     */
    PRE_LOADING {

    },
    LOADING {
        @Override
        public void enter(GameStateManager entity) {
            Strafer.getInstance().setScreen(LoadingScreen.getInstance());
        }
    },
    TITLE_MENU {
        @Override
        public void enter(GameStateManager entity) {
            Strafer.getInstance().setScreen(TitleScreen.getInstance());
        }
    },
    SETTINGS_MENU {
        @Override
        public void enter(GameStateManager entity) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            Strafer.getInstance().setScreen(SettingsScreen.getInstance());
            //suggest to the settings menu to fallback to either the pause menu or title menu
        }

    },
    LOAD_SAVE_MENU {
        @Override
        public void enter(GameStateManager entity) {
            Strafer.getInstance().setScreen(LoadSaveScreen.getInstance());
        }
        @Override
        public void exit(GameStateManager entity) {
            EntityEngine.getInstance().pauseOnSystems(null, false);
            Strafer.uiManager.emptyTrigger();

        }
    },
    PAUSE {
        @Override
        public void enter(GameStateManager entity) {
            EntityEngine.getInstance().pauseOnSystems(null, true);
            Strafer.uiManager.pauseTrigger();
        }

        @Override
        public void exit(GameStateManager entity) {
            EntityEngine.getInstance().pauseOnSystems(null, false);
            Strafer.uiManager.emptyTrigger();

        }
    },
    CUTSCENE,
    PLAY {
        @Override
        public void enter(GameStateManager entity) {
            Strafer.getInstance().setScreen(GameScreen.getInstance());
        }

    };

    @Override
    public void enter(GameStateManager entity) {

    }

    @Override
    public void update(GameStateManager entity) {

    }

    @Override
    public void exit(GameStateManager entity) {

    }

    @Override
    public boolean onMessage(GameStateManager entity, Telegram telegram) {
        return false;
    }
}
