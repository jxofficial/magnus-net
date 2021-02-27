package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    public Table() {
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
        return fileMenu;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
             super(new GridLayout(8,8));
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
            validate();
        }

        private void assignTileColor() {
        }
    }
}
