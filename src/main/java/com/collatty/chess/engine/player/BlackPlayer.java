package com.collatty.chess.engine.player;

import com.collatty.chess.engine.Alliance;
import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.pieces.Piece;

import java.util.Collection;

public class BlackPlayer extends Player{

    public BlackPlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    public Player getOpponent() {
        return null;
    }
}
