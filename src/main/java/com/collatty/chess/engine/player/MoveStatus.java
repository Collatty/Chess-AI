package com.collatty.chess.engine.player;

public enum MoveStatus {

    DONE {
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {

        @Override
        public boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
