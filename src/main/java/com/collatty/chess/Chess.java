package com.collatty.chess;

import com.collatty.chess.engine.board.Board;
import com.collatty.chess.gui.Table;

public class Chess {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        Table table = new Table();
    }
}
