package com.collatty.chess.gui;


import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.BoardUtils;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.board.Tile;
import com.collatty.chess.engine.pieces.Piece;
import com.collatty.chess.engine.player.MoveTransition;
import com.collatty.chess.engine.player.ai.MiniMax;
import com.collatty.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.collatty.chess.engine.board.Move.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;


public class Table extends Observable {

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private static final Color lightTileColor = Color.decode("#FFFACD");
    private static final Color darkTileColor = Color.decode("#593E1A");

    private String defaultPieceImagesPath = "art/pieceImages/holywarriors/";


    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private Board chessBoard;
    private final GameSetup gameSetup;
    private Move computerMove;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;


    private boolean highlightLegalMoves;

    private static final Table INSTANCE = new Table();

    private Table() {
        this.chessBoard = Board.createStandardBoard();
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setLayout(new BorderLayout());
        this.highlightLegalMoves = false;
        this.boardDirection = BoardDirection.NORMAL;
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setVisible(true);
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(this.chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPrefrencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {

        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file!");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPrefrencesMenu() {
        final JMenu prefrencesMenu = new JMenu("Prefrences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        prefrencesMenu.add(flipBoardMenuItem);
        prefrencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal moves", false);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });
        prefrencesMenu.add(legalMoveHighlighterCheckbox);

        return prefrencesMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionsMenu.add(setupGameMenuItem);

        return optionsMenu;
    }

    public static Table get() {
        return INSTANCE;
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
                !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                //create AI thread
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
                System.out.println("game over checkmate");
                }

            if(Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
                System.out.println("game over stalemate");

            }
        }
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {

        }

        @Override
        protected Move doInBackground() throws Exception {

            final MoveStrategy miniMax = new MiniMax(4);

            final Move bestMove = miniMax.execute(Table.get().getGameBoard());

            return bestMove;
        }

        @Override
        public void done() {
            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveMadeUpdate(PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel() {
        return this.takenPiecesPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private void updateComputerMove(Move bestMove) {
        this.computerMove = bestMove;
    }


    public void updateGameBoard(Board transitionBoard) {
        this.chessBoard = transitionBoard;
    }

    private Board getGameBoard(){
        return this.chessBoard;
    }

    private class BoardPanel extends JPanel {

        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<TilePanel>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();

        }

        public void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<Move>();

        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove (final Move move) {
            this.moves.add(move);
        }
        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move){
            return this.moves.remove(move);
        }


    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            addMouseListener(new MouseListener() {
                public void mouseClicked(final MouseEvent e) {

                    if(isRightMouseButton(e)) {

                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if( isLeftMouseButton(e)) {
                        if(sourceTile == null) {
                            //first click
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            //second click
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);

                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });

                    }

                }

                public void mousePressed(final MouseEvent e) {

                }

                public void mouseReleased(final MouseEvent e) {

                }

                public void mouseEntered(final MouseEvent e) {

                }

                public void mouseExited(final MouseEvent e) {

                }
            });
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()){
                try {
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId)
                                    .getPiece().getPieceAlliance()
                                    .toString().substring(0,1) + board.getTile(this.tileId).toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId] ||
                BoardUtils.SIXTH_RANK[this.tileId] ||
                BoardUtils.FOURTH_RANK[this.tileId] ||
                BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if ( BoardUtils.SEVENTH_RANK[this.tileId] ||
                        BoardUtils.FIFTH_RANK[this.tileId] ||
                        BoardUtils.THIRD_RANK[this.tileId] ||
                        BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }


        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();

        }

        private void highlightLegals(final Board board) {
            if(highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)){
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }
    }

    public enum BoardDirection {

        NORMAL{
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();

    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }
}
