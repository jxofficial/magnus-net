package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    final Board board;
    final Piece pieceMoved;
    final int destinationCoordinate;

    public Move(Board board, Piece pieceMoved, int destinationCoordinate) {
        this.board = board;
        this.pieceMoved = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public abstract Board execute();

    public static final class CapturingMove extends Move {
        final Piece attackedPiece;

        public CapturingMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static final class NormalMove extends Move {
        public NormalMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // for every of player's active pieces, place it on the board
            // except the piece that is currently being moved
            for (final Piece p : this.board.currentPlayer().getActivePieces()) {
                // TODO implement hashcode and equals for Piece
                if (!this.pieceMoved.equals(p)) {
                    builder.setPiece(p);
                }
            }

            // set all of opponent's pieces on the board
            for (final Piece p : this.board.currentPlayer().getActivePieces()) {
                builder.setPiece(p);
            }

            // TODO set current piece that is being moved
            builder.setPiece(null);
            // update next player to move
            builder.setNextMoveMaker((this.board.currentPlayer().getOpponent().getAlliance()));

            return builder.build();
        }
    }


}
