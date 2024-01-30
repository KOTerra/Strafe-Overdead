package com.strafergame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;
import com.strafergame.screens.GameScreen;
import com.strafergame.screens.SettingsScreen;
import com.strafergame.screens.TitleScreen;

//pause and add ecs systems and listeners based on the gamestate
public enum GameStateType implements State<GameStateManager> {
    /**
     * Called on Launcher? Maybe not ok to manage states before game creation
     * <p>
     * Mandatory settings to be read from preferences that are required at app start such as fullscreen mode
     */
    PRE_LOADING,
    LOADING,
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
