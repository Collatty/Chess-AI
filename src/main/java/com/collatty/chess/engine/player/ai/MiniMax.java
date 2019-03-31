package com.collatty.chess.engine.player.ai;

import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;

    public MiniMax() {
        this.boardEvaluator = null;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }


    @Override
    public Move execute(Board board, int depth) {
        return null;
    }
}
