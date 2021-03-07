package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.CastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;

    private Board chessboard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private BoardDirection boardDirection;

    private boolean shouldHighlightLegalMoves;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final Color LIGHT_TILE_COLOR = Color.decode("#eeeed2");
    private final Color DARK_TILE_COLOR = Color.decode("#769656");

    public Table() {
        // Board is a list of Tiles
        // which keeps track of white, black, current players
        // and also pieces
        this.chessboard = Board.createStandardBoard();
        this.moveLog = new MoveLog();

        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        // preferences
        this.shouldHighlightLegalMoves = true;

        // window
        this.gameFrame = new JFrame("magnus-net");
        this.gameFrame.setLayout(new BorderLayout());
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

        // menu bar
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);

        // board
        this.boardPanel = new BoardPanel();
        this.boardDirection = BoardDirection.NORMAL;

        // window
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(actionEvent -> {
            System.out.println("Open file explorer");
        });

        fileMenu.add(openPGN);

        final JMenuItem exit = new JMenuItem("Quit");
        exit.addActionListener(actionEvent -> System.exit(0));
        fileMenu.add(exit);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoard = new JMenuItem("Flip Board");
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessboard);
            }
        });
        preferencesMenu.add(flipBoard);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem shouldHighlightLegalMovesCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves",
                true);
        shouldHighlightLegalMovesCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shouldHighlightLegalMoves = shouldHighlightLegalMovesCheckbox.isSelected();
            }
        });
        preferencesMenu.add(shouldHighlightLegalMovesCheckbox);

        return preferencesMenu;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
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
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {
        private final int tileCoordinate;

        TilePanel(final BoardPanel boardPanel, final int tileCoordinate) {
            super(new GridBagLayout());
            this.tileCoordinate = tileCoordinate;
            setPreferredSize(TILE_PANEL_DIMENSION);
            // basically these two fns are encapsulated in TilePanel#drawTile (which also includes
            // BoardPanel#highlightLegalMoves)
            // drawTile gn used in BoardPanel#drawBoard for all subsequent re-renders of the board
            assignTileColor();
            assignTilePieceIcon(chessboard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)) {
                        // reset all
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else if (isLeftMouseButton(e)) {
                        // first click
                        if (sourceTile == null) {
                            sourceTile = chessboard.getTile(tileCoordinate);
                            humanMovedPiece = sourceTile.getPiece();
                            // if empty tile is selected, don't set anything for source tile
                            // assume its a mis-click
                            if (humanMovedPiece == null) sourceTile = null;

                        } else { // second click
                            destinationTile = chessboard.getTile(tileCoordinate);
                            // here you can still click on opposite's player's pieces
                            // source and destination, and the move will still be returned
                            final Move move = Move.MoveFactory.createMove(
                                    chessboard,
                                    sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            // here, you check whether the player is allowed to make the move
                            final MoveTransition transition = chessboard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessboard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }

                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        // always redraw the board after ANY and ALL left clicks
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessboard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessboard);
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            final String localDir = System.getProperty("user.dir");
            final String PIECE_ICON_ROOT_PATH = "\\art\\pieces\\";

            this.removeAll();
            if (board.getTile(this.tileCoordinate).isTileOccupied()) {

                String pieceAlliancePrefix = board
                        .getTile(this.tileCoordinate)
                        .getPiece()
                        .getPieceAlliance()
                        .toString()
                        .substring(0, 1);

                String pieceName = board
                        .getTile(this.tileCoordinate)
                        .getPiece()
                        .toString();

                // eg art/basic-pieces/BK.gif for black king
                String iconFilePath = localDir + PIECE_ICON_ROOT_PATH
                        + pieceAlliancePrefix + pieceName + ".gif";
                try {
                    final BufferedImage img = ImageIO.read(
                            new File(iconFilePath));
                    add(new JLabel(new ImageIcon(img)));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        private void assignTileColor() {
            // where ever I am, I + my row
            boolean isLightTile = ((tileCoordinate + tileCoordinate / 8) % 2 == 0);
            setBackground(isLightTile ? LIGHT_TILE_COLOR : DARK_TILE_COLOR);
        }

        // TODO: highlight castle moves that do not
        private void highlightLegalMoves(final Board board) {
            if (shouldHighlightLegalMoves) {
                // running calculatePieceToBeMovedLegalMoves(board) ensures
                // 1. there is a clicked piece
                // 2. clicked piece belongs to current player
                for (final Move move : calculatePieceToBeMovedLegalMoves(board)) {
                    MoveTransition transition = board.currentPlayer().makeMove(move);
                    if(!transition.getMoveStatus().isDone()){
                        continue;
                    }
                    if (move.getDestinationCoordinate() == this.tileCoordinate) {
                        try {
                            ImageIcon greenDot = new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")));
                            add(new JLabel(greenDot));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> calculatePieceToBeMovedLegalMoves(final Board board) {
            if (humanMovedPiece != null
                    && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                if (humanMovedPiece instanceof King) {
                    Collection<CastleMove> castleMoves = board.currentPlayer().calculateCastleMoves(
                            board.currentPlayer().getLegalMoves(),
                            board.currentPlayer().getOpponent().getLegalMoves()
                    );

                    return ImmutableList.copyOf(
                            Iterables.concat(castleMoves, humanMovedPiece.calculateLegalMoves(board)));
                } else {
                    return humanMovedPiece.calculateLegalMoves(board);
                }
            }
            return Collections.emptyList();
        }
    }


    public static class MoveLog {
        private final List<Move> moves;

        public MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int i) {
            return this.moves.remove(i);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }

    }

    public enum BoardDirection {
        NORMAL {
            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }
        },
        FLIPPED {
            @Override
            BoardDirection opposite() {
                return NORMAL;
            }

            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }
        };

        abstract BoardDirection opposite();

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
    }
}
