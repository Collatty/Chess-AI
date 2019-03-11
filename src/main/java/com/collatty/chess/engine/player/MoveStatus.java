package com.collatty.chess.engine.player;

public enum MoveStatus {

    DONE {
        boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {

        @Override
        boolean isDone() {
            return false;
        }
    },
    LEAVES_PLAYER_IN_CHECK {
        boolean isDone() {
            return false;
        }
    };

    abstract boolean isDone();
}
