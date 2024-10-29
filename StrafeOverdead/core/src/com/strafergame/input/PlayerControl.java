package com.strafergame.input;

import com.badlogic.gdx.Input;

import java.util.ArrayDeque;
import java.util.ArrayList;

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


    public static final int SEQUENCE_TIMEFRAME = 500;//ms
    public static final int DEFAULT_SEQUENCE_CAPACITY = 10;
    public static ActionSequence<ActionSequenceElement> actionSequence = new ActionSequence<>(DEFAULT_SEQUENCE_CAPACITY);

    public static class ActionSequence<ActionSequenceElement> extends ArrayDeque<ActionSequenceElement> {
        int capacity;

        public ActionSequence(int capacity) {
            this.capacity = capacity;
        }

        /**
         * checks whether the sequence of size=length has elements added at intervals shorter than time from one another
         *
         * @param time in milliseconds
         */
        public boolean isInTimeframe(int length, int time) {
            boolean result = true;

            Object[] arr = this.toArray();
            if (arr.length < length) {
                return false;
            }
            if (arr[0] instanceof PlayerControl.ActionSequenceElement first) {
                if (System.currentTimeMillis() - first.time > SEQUENCE_TIMEFRAME) {
                    result = false;
                }
            }
            for (int i = 0; i < length - 1; i++) {
                if (arr[i] instanceof PlayerControl.ActionSequenceElement a && arr[i + 1] instanceof PlayerControl.ActionSequenceElement b) {
                    if (a.time - b.time > time) {
                        result = false;
                    }
                }

            }
            return result;
        }

        /**
         * Retrieves all the keycodes of the most recent length keys in the sequence
         *
         * @param length
         */
        public ArrayList<Integer> getSequenceKeycodes(int length) {
            ArrayList<Integer> keys = new ArrayList<>();
            Object[] arr = this.toArray();
            if (arr.length < length) {
                return new ArrayList<>();
            }
            for (int i = 0; i < length; i++) {
                if (arr[length - i - 1] instanceof PlayerControl.ActionSequenceElement a) {
                    keys.add(a.keycode);
                }
            }
            return keys;
        }

        public void clean() {
            if (this.size() > capacity) {
                this.removeLast();
            }
        }

        @Override
        public void addFirst(ActionSequenceElement element) {
            super.addFirst(element);
            this.clean();
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

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
            if (keycode < 0) {
                return "negative " + time;
            }
            return Input.Keys.toString(keycode) + ' ' + time;
        }
    }
}
