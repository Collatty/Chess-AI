package com.collatty.chess;

import com.collatty.chess.engine.board.Board;
import com.collatty.chess.gui.Table;

public class Chess {

    public static void main(String[] args) {
        Board board = Board.createStandardBoard();

        System.out.println(board);

        Table table = new Table();
    }
}
