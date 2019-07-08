package com.collatty.chess;


import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.player.ai.MiniMax;
import com.collatty.chess.pgn.FenUtilities;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChessController {

    @RequestMapping("/play")
    public String playMove(@RequestParam String fen) {
        Board board = FenUtilities.createGameFromFEN(fen);
        MiniMax miniMax = new MiniMax(4);
        if (board == null) {
            return "Something went wrong with the fen-string";
        }
        return FenUtilities.createFENFromGame(board.currentPlayer().makeMove(miniMax.execute(board)).getTransitionBoard());
    }
}
