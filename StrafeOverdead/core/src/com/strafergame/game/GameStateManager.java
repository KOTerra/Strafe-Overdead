package com.strafergame.game;

import com.badlogic.gdx.ai.fsm.StackStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;

public class GameStateManager {
    private static GameStateManager instance;
    private final StateMachine<GameStateManager, GameStateType> stateMachine;

    public GameStateManager() {
        stateMachine = new StackStateMachine<>(this);
        stateMachine.setInitialState(GameStateType.MENU);
    }

    public void update() {
        stateMachine.update();

    }

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public StateMachine getStateMachine() {
        return this.stateMachine;
    }


}
