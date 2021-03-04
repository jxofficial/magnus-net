package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final Color LIGHT_TILE_COLOR = Color.decode("#eeeed2");
    private final Color DARK_TILE_COLOR = Color.decode("#769656");

    public Table() {
        this.chessBoard = Board.createStandardBoard();

        this.gameFrame = new JFrame("magnus-net");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);

        this.gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());

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

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                this.add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel: boardTiles) {
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
            assignTileColor();
            assignTilePieceIcon(chessBoard);

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
                            sourceTile = chessBoard.getTile(tileCoordinate);
                            humanMovedPiece = sourceTile.getPiece();
                            // if empty tile is selected, don't set anything for source tile
                            // assume its a mis-click
                            if (humanMovedPiece == null) sourceTile = null;
                        } else { // second click
                            destinationTile = chessBoard.getTile(tileCoordinate);
                            final Move move = Move.MoveFactory.createMove(
                                    chessBoard,
                                    sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                // TODO: add move that was made to move log
                            }

                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    boardPanel.drawBoard(chessBoard);
                                }
                            });
                        }
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
    }
}
