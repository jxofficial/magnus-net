package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    final Board board;
    final Piece pieceToBeMoved;
    final int destinationCoordinate;

    public Move(Board board, Piece pieceMoved, int destinationCoordinate) {
        this.board = board;
        this.pieceToBeMoved = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
    }

    /* Abstract methods*/
    public abstract Board execute();

    /* Getters */
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getPieceToBeMoved() {
        return this.pieceToBeMoved;
    }
    /* Nested clases */
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
                if (!this.pieceToBeMoved.equals(p)) {
                    builder.setPiece(p);
                }
            }

            // set all of opponent's pieces on the board
            for (final Piece p : this.board.currentPlayer().getActivePieces()) {
                builder.setPiece(p);
            }

            // the piece to be moved will call its method on the Piece class
            // which takes in a move and returns a new Piece in the desired position
            // as defined by the move that is passed in.
            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            // update next player to move
            builder.setNextMoveMaker((this.board.currentPlayer().getOpponent().getAlliance()));

            return builder.build();
        }
    }
}
