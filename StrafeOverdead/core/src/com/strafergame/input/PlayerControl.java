package com.strafergame.input;

import com.badlogic.gdx.Input;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class PlayerControl {

    public enum ActionType {
        MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT, JUMP, DASH
    }

    public static boolean MOVE_UP = false;

    public static boolean MOVE_LEFT = false;

    public static boolean MOVE_DOWN = false;

    public static boolean MOVE_RIGHT = false;

    public static boolean JUMP = false;

    public static boolean DASH = false;

    public static ActionSequence<ActionSequenceElement> actionSequence = new ActionSequence<>();


    /**
     *
     */
    public static class ActionSequenceElement {
        private int keycode;
        private long time;

        public ActionSequenceElement(int keycode, long time) {
            this.keycode = keycode;
            this.time = time;
        }

        public String toString() {
            return Input.Keys.toString(keycode)+' '+ time;
        }
    }

    public static class ActionSequence<ActionSequenceElement> extends ArrayDeque<ActionSequenceElement> {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Object e : this.toArray()) {
                if (e != null) {
                    sb.append(e.toString());
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }
}
