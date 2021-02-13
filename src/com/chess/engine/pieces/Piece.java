package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;

    public Piece(Alliance pieceAlliance, int piecePosition, final PieceType pieceType) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        // TODO update this
        this.isFirstMove = false;
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
