package com.collatty.chess.engine.pieces;


import com.collatty.chess.engine.Alliance;
import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = false;

    }

    public int getPiecePosition() {
        return this.piecePosition;
    }
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public enum PieceType {

        PAWN("P") {
            public boolean isKing() {
                return false;
            }
        },

        KNIGHT("N") {
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B") {
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R") {
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("Q") {
            public boolean isKing() {
                return false;
            }
        },
        KING("K") {
            public boolean isKing() {
                return true;
            }
        };

        private String pieceName;

        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
    }




}
