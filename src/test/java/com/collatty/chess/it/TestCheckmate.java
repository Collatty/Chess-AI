package com.collatty.chess.it;


import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.BoardUtils;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.player.MoveTransition;
import com.collatty.chess.engine.player.ai.MiniMax;
import com.collatty.chess.engine.player.ai.MoveStrategy;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TestCheckmate {

    @Test
    public void testFoolsMate() {

        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.getCoordinateAtPosition("f2"),
                        BoardUtils.getCoordinateAtPosition("f3")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getTransitionBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("e7"),
                        BoardUtils.getCoordinateAtPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getTransitionBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("g2"),
                        BoardUtils.getCoordinateAtPosition("g4")));

        assertTrue(t3.getMoveStatus().isDone());

        final MoveStrategy moveStrategy = new MiniMax(4);

        final Move aiMove = moveStrategy.execute(t3.getTransitionBoard());

        final Move bestMove = Move.MoveFactory.createMove(t3.getTransitionBoard(),
                BoardUtils.getCoordinateAtPosition("d8"), BoardUtils.getCoordinateAtPosition("h4"));

        final MoveTransition t4 = t3.getTransitionBoard()
                .currentPlayer()
                .makeMove(Move.MoveFactory.createMove(t3.getTransitionBoard(), BoardUtils.getCoordinateAtPosition("d8"),
                        BoardUtils.getCoordinateAtPosition("h4")));

        assertTrue(t4.getMoveStatus().isDone());

        assertTrue(t4.getTransitionBoard().currentPlayer().isInCheckMate());
        assertEquals(aiMove, bestMove);

    }

}