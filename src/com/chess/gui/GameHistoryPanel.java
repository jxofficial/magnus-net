package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.gui.Table.MoveLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100,400);

    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane  = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveHistory) {
        int currRow = 0;
        this.model.clear();
        for (final Move m: moveHistory.getMoves()) {
            final String movePGN = m.toString();
            if (m.getPieceToBeMoved().getPieceAlliance().isWhite()) {
                this.model.setValueAt(movePGN, currRow, 0);
            } else if (m.getPieceToBeMoved().getPieceAlliance().isBlack()) {
                this.model.setValueAt(movePGN, currRow, 1);
                currRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            String movePGN = lastMove.toString();

            if (lastMove.getPieceToBeMoved().getPieceAlliance().isWhite()) {
                // add either + or # as needed
                this.model.setValueAt(movePGN + calculateCheckANdCheckMateHash(board), currRow - 1, 1);
            }

            // set it to always be at the last move
            final JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        }
    }

    private String calculateCheckANdCheckMateHash(final Board board) {
        if (board.currentPlayer().isCheckmated()) {
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        } else {
            return "";
        }
    }

    private static class DataModel extends DefaultTableModel {
        private final List<Row> rows;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.rows = new ArrayList<>();
        }

        public void clear() {
            this.rows.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.rows == null) {
                return 0;
            }
            return this.rows.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.rows.get(row);
            if (column == 0) {
                return currentRow.getWhiteMove();
            } else if (column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object val, int row, int column) {
            final Row currRow;
            if (this.rows.size() <= row) {
                currRow = new Row();
                this.rows.add(currRow);
            } else {
                currRow = this.rows.get(row);
            }

            if (column == 0) {
                currRow.setWhiteMove((String) val);
            } else if (column == 1) {
                currRow.setBlackMove((String) val);
            }
        }


        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return NAMES[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        public Row() {
        }

        public String getWhiteMove() {
            return whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }
    }


}
