package com.chess.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Move;
import com.chess.gui.Table.MoveLog;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

public class TakenPiecesPanel extends JPanel {
    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40, 80);

    public TakenPiecesPanel() {
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);

        // 8 rows, 2 cols
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel.setBackground(PANEL_COLOR);

        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);

        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final MoveLog movelog) {
        northPanel.removeAll();
        southPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for (final Move m : movelog.getMoves()) {
            if (m.isCapturingMove()) {
                final Piece takenPiece = m.getAttackedPiece();
                Alliance alliance = takenPiece.getPieceAlliance();
                if (alliance.isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else if (alliance.isBlack()) {
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("Piece can only be white or black.");
                }
            }
        }

        Collections.sort(whiteTakenPieces, (p1, p2) -> Ints.compare(p1.getPieceValue(), p2.getPieceValue()));
        Collections.sort(blackTakenPieces, (p1, p2) -> Ints.compare(p1.getPieceValue(), p2.getPieceValue()));

        for (final Piece takenPiece : whiteTakenPieces) {
            try {
                final String localDir = System.getProperty("user.dir");
                final String PIECE_ICON_ROOT_PATH = "\\art\\pieces\\";

                String pieceAlliancePrefix = takenPiece
                        .getPieceAlliance()
                        .toString()
                        .substring(0, 1);

                String pieceName = takenPiece.toString();

                // eg art/basic-pieces/BN.gif for black knight
                String imgFilePath = localDir + PIECE_ICON_ROOT_PATH
                        + pieceAlliancePrefix + pieceName + ".gif";

                final BufferedImage img = ImageIO.read(new File(imgFilePath));
                final ImageIcon icon = new ImageIcon(img);
                final JLabel imgLabel = new JLabel(icon);

                this.northPanel.add(imgLabel);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        for (final Piece takenPiece : blackTakenPieces) {
            try {
                final String localDir = System.getProperty("user.dir");
                final String PIECE_ICON_ROOT_PATH = "\\art\\pieces\\";

                String pieceAlliancePrefix = takenPiece
                        .getPieceAlliance()
                        .toString()
                        .substring(0, 1);

                String pieceName = takenPiece.toString();

                // eg art/basic-pieces/BK.gif for black king
                String imgFilePath = localDir + PIECE_ICON_ROOT_PATH
                        + pieceAlliancePrefix + pieceName + ".gif";

                final BufferedImage img = ImageIO.read(new File(imgFilePath));
                final ImageIcon icon = new ImageIcon(img);
                final JLabel imgLabel = new JLabel(icon);

                this.southPanel.add(imgLabel);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        validate();
    }
}
