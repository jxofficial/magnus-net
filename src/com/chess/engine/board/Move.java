package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import java.util.Objects;

import static com.chess.engine.board.Board.Builder;

public abstract class Move {
    protected final Board board;
    protected final Piece pieceToBeMoved;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    public Move(Board board, Piece pieceMoved, int destinationCoordinate) {
        this.board = board;
        this.pieceToBeMoved = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = pieceMoved.isFirstMove();
    }

    public Move(Board board, int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.pieceToBeMoved = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationCoordinate, pieceToBeMoved.hashCode(), pieceToBeMoved.getPiecePosition());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Move)) return false;

        final Move otherMove = (Move) other;
        return getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getPieceToBeMoved().equals(otherMove.getPieceToBeMoved()) &&
                getCurrCoordinate() == otherMove.getCurrCoordinate();

    }

    public boolean isCastlingMove() {
        return false;
    }

    public boolean isCapturingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
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
        for (final Piece p : this.board.currentPlayer().getOpponent().getActivePieces()) {
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

    public int getCurrCoordinate() {
        return this.pieceToBeMoved.getPiecePosition();
    }

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
        public boolean isCapturingMove() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
    }

    public static final class NormalMove extends Move {
        public NormalMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof NormalMove)) return false;

            return super.equals(other);
        }

        @Override
        public String toString() {
            return pieceToBeMoved.getPieceType().toString()
                    + BoardUtils.getPGNFromCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public String toString() {
            return BoardUtils.getPGNFromCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other ||
                    (other instanceof PawnMove && super.equals(other));
        }

    }

    public static class PawnCapturingMove extends CapturingMove {
        public PawnCapturingMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean isCapturingMove() {
            return true;
        }

        @Override
        public boolean equals(Object other) {
            return this == other ||
                    (other instanceof PawnCapturingMove && super.equals(other));
        }

        @Override
        public String toString() {
            // eg exd4
            return BoardUtils.getPGNFromCoordinate(this.pieceToBeMoved.getPiecePosition()).charAt(0)
                    + "x" + BoardUtils.getPGNFromCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantMove extends PawnCapturingMove {
        public PawnEnPassantMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other ||
                    (other instanceof PawnEnPassantMove && super.equals(other));
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece p : this.board.currentPlayer().getActivePieces()) {
                if (!this.pieceToBeMoved.equals(p)) {
                    builder.setPiece(p);
                }
            }

            for (final Piece p : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!p.equals(this.getAttackedPiece())) {
                    builder.setPiece(p);
                }
            }

            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            builder.setNextMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnJumpMove extends Move {
        public PawnJumpMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }

        @Override
        public String toString() {
            return BoardUtils.getPGNFromCoordinate(this.destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            this.board.currentPlayer().getActivePieces().stream().filter(piece -> !this.pieceToBeMoved.equals(piece)).forEach(builder::setPiece);
            this.board.currentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
            final Pawn movedPawn = (Pawn) this.pieceToBeMoved.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setNextMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    public static abstract class CastleMove extends Move {
        protected final Rook rook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove(final Board board,
                          final Piece pieceMoved,
                          final int destinationCoordinate,
                          final Rook rook,
                          final int castleRookStart,
                          final int castleRookDestination
        ) {
            super(board, pieceMoved, destinationCoordinate);
            this.rook = rook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            // this.pieceToBeMoved is the king
            for (final Piece p : this.board.currentPlayer().getActivePieces()) {
                // set everything to be the same as original board
                // except for rook and king
                if (!this.pieceToBeMoved.equals(p) && !this.rook.equals(p)) {
                    builder.setPiece(p);
                }
            }

            // set all of opponent's pieces on the board
            for (final Piece p : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(p);
            }

            builder.setPiece((this.pieceToBeMoved.movePiece(this)));
            // TODO: set isFirstMove in Rook piece to false
            builder.setPiece(new Rook(this.rook.getPieceAlliance(), this.castleRookDestination));

            builder.setNextMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

            // returns new Board(builder);
            return builder.build();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.rook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof CastleMove)) {
                return false;
            }

            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.rook.equals(otherCastleMove.rook);
        }
    }

    // needs to be static so that you can instantiate it without an instance of Move
    // Move cannot be instantiated anyway
    public static final class KingsideCastleMove extends CastleMove {
        public KingsideCastleMove(Board board, Piece pieceMoved, int destinationCoordinate, Rook rook,
                                  int castleRookStart, int castleRookDestination) {
            super(board, pieceMoved, destinationCoordinate, rook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0";
        }

        @Override
        public boolean equals(Object other) {
            return this == other ||
                    (other instanceof KingsideCastleMove && super.equals(other));
        }
    }

    // needs to be static so that you can instantiate it without an instance of Move
    // Move cannot be instantiated anyway
    public static final class QueensideCastleMove extends CastleMove {
        public QueensideCastleMove(Board board, Piece pieceMoved, int destinationCoordinate,
                                   Rook rook, int castleRookStart, int castleRookDestination) {
            super(board, pieceMoved, destinationCoordinate, rook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString() {
            return "0-0-0";
        }
        @Override
        public boolean equals(Object other) {
            return this == other ||
                    (other instanceof QueensideCastleMove && super.equals(other));
        }

    }

    public static final class InvalidMove extends Move {
        public InvalidMove() {
            super(null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Invalid Move cannot be executed");
        }
    }

    public static class MoveFactory {
        public static final Move INVALID_MOVE = new InvalidMove();
        private MoveFactory() {
            throw new RuntimeException("MoveFactory cannot be instantiated");
        }

        // return a move given the board and the intended coordinates
        public static Move createMove(final Board board, final int currCoordinate, final int destinationCoordinate) {
            for (final Move m : board.getAllLegalMoves()) {
                if (m.getCurrCoordinate() == currCoordinate
                        && m.getDestinationCoordinate() == destinationCoordinate
                ) return m;
            }

            return INVALID_MOVE;
        }
    }
}
