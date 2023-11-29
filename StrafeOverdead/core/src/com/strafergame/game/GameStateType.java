package com.strafergame.game;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.strafergame.Strafer;
import com.strafergame.game.ecs.EntityEngine;

//pause and add ecs systems and listeners based on the gamestate
public enum GameStateType implements State<GameStateManager> {
    LOADING,
    MENU,
    PAUSE {
        public void enter(GameStateManager entity) {
            EntityEngine.getInstance().pauseOnSystems(null,true);
            Strafer.uiManager.pauseTrigger();
        }
        public void exit(GameStateManager entity) {
            EntityEngine.getInstance().pauseOnSystems(null,false);
        }
    },
    CUTSCENE,
    PLAY{

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
