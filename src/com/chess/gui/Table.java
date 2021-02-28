package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board chessBoard;

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
    }

    private class TilePanel extends JPanel {
        private final int tileCoordinate;

        TilePanel(final BoardPanel boardPanel, final int tileCoordinate) {
            super(new GridBagLayout());
            this.tileCoordinate = tileCoordinate;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            validate();
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
