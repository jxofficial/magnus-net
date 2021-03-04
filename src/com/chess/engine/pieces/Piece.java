package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;
import java.util.Objects;

public abstract class Piece {
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;

    public Piece(Alliance pieceAlliance, int piecePosition, final PieceType pieceType, final Boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
    }


    /* Abstract methods */
    public abstract Collection<Move> calculateLegalMoves(Board board);
    // returns a new piece given a move
    public abstract Piece movePiece(Move move);

    /* Class method */
    public boolean isFirstMove() {
        return isFirstMove;
    }

    /* Getters */
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }
    public int getPiecePosition() { return this.piecePosition; }
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Piece)) return false;
        final Piece otherPiece = (Piece) other;
        return pieceAlliance == otherPiece.getPieceAlliance()
                && pieceType == otherPiece.getPieceType()
                && piecePosition == otherPiece.getPiecePosition()
                && isFirstMove == otherPiece.isFirstMove();

    }

    /* Nested enum */
    public enum PieceType {
        PAWN("P") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KNIGHT("N") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        BISHOP("B") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        ROOK("R") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        QUEEN("Q") {
            @Override
            public boolean isKing() {
                return false;
            }
        },
        KING("K") {
            @Override
            public boolean isKing() {
                return true;
            }
        };

        private String pieceName;
        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
    }
}
