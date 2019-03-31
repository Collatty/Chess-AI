package com.collatty.chess.engine.player.ai;

import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;

public interface MoveStrategy {

    Move execute(Board board);

}
