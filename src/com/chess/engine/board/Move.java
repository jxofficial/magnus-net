package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Move {
    final Board board;
    final Piece pieceMoved;
    final int destinationCoordinate;

    public Move(Board board, Piece pieceMoved, int destinationCoordinate) {
        this.board = board;
        this.pieceMoved = pieceMoved;
        this.destinationCoordinate = destinationCoordinate;
    }

    public static final class CapturingMove extends Move {
        final Piece attackedPiece;

        public CapturingMove(Board board, Piece pieceMoved, int destinationCoordinate, Piece attackedPiece) {
            super(board, pieceMoved, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }
    }

    public static final class PieceMove extends Move {
        public PieceMove(Board board, Piece pieceMoved, int destinationCoordinate) {
            super(board, pieceMoved, destinationCoordinate);
        }
    }


}
