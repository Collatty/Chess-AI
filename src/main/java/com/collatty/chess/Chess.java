package com.collatty.chess;

import com.collatty.chess.engine.board.Board;
import com.collatty.chess.gui.Table;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Chess {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Chess.class, args);

        //Table.get().show();
    }
}
