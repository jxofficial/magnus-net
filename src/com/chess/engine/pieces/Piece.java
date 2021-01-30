package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;

    public Piece(Alliance pieceAlliance, int piecePosition) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        // TODO update this
        this.isFirstMove = false;
    }

    public abstract Collection<Move> calculateLegalMoves(Board board);

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public int getPiecePosition() { return this.piecePosition; }
}
