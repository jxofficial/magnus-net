package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

import static com.chess.engine.board.Board.*;

public abstract class Move {
    final Board board;
    final Piece pieceToBeMoved;
    final int destinationCoordinate;
    public static final Move INVALID_MOVE = new InvalidMove();

    public Move(Board board, Piece pieceMoved, int destinationCoordinate) {
        this.board = board;
        this.pieceToBeMoved = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
    }

    public Board execute() {
        final Builder builder = new Builder();
        // for every of player's active pieces, place it on the board
        // except the piece that is currently being moved
        for (final Piece p : this.board.currentPlayer().getActivePieces()) {
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

    /* Getters */
    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }
    public int getCurrCoordinate() { return this.pieceToBeMoved.getPiecePosition(); }

    public Piece getPieceToBeMoved() {
        return this.pieceToBeMoved;
    }

    /* Nested classes */
    public static class CapturingMove extends Move {
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
    }

    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }

    public static class PawnCapturingMove extends CapturingMove {
        public PawnCapturingMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate, attackedPiece);
        }
    }

    public static final class PawnEnPassantMove extends PawnCapturingMove {
        public PawnEnPassantMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate, attackedPiece);
        }
    }

    public static final class PawnJumpMove extends Move {
        public PawnJumpMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move {
        public CastleMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }

    public final class KingsideCastleMove extends Move {
        public KingsideCastleMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }

    public final class QueensideCastleMove extends Move {
        public QueensideCastleMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }

    public static final class InvalidMove extends Move {
        public InvalidMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Invalid Move cannot be executed");
        }
    }

    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("MoveFactory cannot be instantiated");
        }

        public static Move createMove(final Board board, final int currCoordinate, final int destinationCooridnate) {
            for (final Move m : board.getAllLegalMoves()) {
                if(m.getCurrCoordinate() == currCoordinate
                    && m.getDestinationCoordinate() == destinationCooridnate
                ) return m;
            }

            return INVALID_MOVE;
        }
    }
}
