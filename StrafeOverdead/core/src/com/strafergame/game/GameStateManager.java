package com.strafergame.game;

import com.badlogic.gdx.ai.fsm.StackStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;

public class GameStateManager {
    private static GameStateManager instance;
    private final StateMachine<GameStateManager, GameStateType> stateMachine;

    public GameStateManager() {
        stateMachine = new StackStateMachine<>(this);
        stateMachine.setInitialState(GameStateType.PRE_LOADING);
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

    public static boolean isState(GameStateType type){
        return getInstance().stateMachine.isInState(type);
    }
    public static void changeState(GameStateType type){
        getInstance().stateMachine.changeState(type);
    }
    public static GameStateType getState(){
        return getInstance().stateMachine.getCurrentState();
    }

}
