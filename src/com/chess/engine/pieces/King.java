package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int destinationCoordinate = this.piecePosition + candidateOffset;

            if (isFirstColumnExclusion(this.piecePosition, candidateOffset)
                || isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {
                final Tile destinationTile = board.getTile(destinationCoordinate);
                if (!destinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.NormalMove(board, this, destinationCoordinate));
                } else {
                    final Piece pieceAtDestination = destinationTile.getPiece();
                    if (this.pieceAlliance != pieceAtDestination.pieceAlliance) {
                        legalMoves.add(new Move.CapturingMove(board, this, destinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }


        return legalMoves;
    }

    private static boolean isFirstColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -9
                || candidateOffset == -1 || candidateOffset == 7));
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 9
                || candidateOffset == 1 || candidateOffset == -7));
    }
}
