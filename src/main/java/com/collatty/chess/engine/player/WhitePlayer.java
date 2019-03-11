package com.collatty.chess.engine.player;

import com.collatty.chess.engine.Alliance;
import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.pieces.Piece;

import java.util.Collection;

public class WhitePlayer extends Player{

    public WhitePlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    public Player getOpponent() {
        return null;
    }
}
