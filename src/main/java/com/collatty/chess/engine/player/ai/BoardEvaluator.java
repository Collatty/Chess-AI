package com.collatty.chess.engine.player.ai;

import com.collatty.chess.engine.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board,int depth);
}
