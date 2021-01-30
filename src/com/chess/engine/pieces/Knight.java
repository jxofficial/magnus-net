package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, -15, 17};

    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(pieceAlliance, piecePosition);
    }

    private static boolean isFirstColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateOffset == -17 || candidateOffset == -10
                || candidateOffset == 6 || candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && (candidateOffset == -10
                || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && (candidateOffset == -6
                || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == 17 || candidateOffset == 10
                || candidateOffset == -6 || candidateOffset == -15));
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestination;
            candidateDestination = this.piecePosition + candidateOffset;
            if (isFirstColumnExclusion(this.piecePosition, candidateOffset)
                    || isSecondColumnExclusion(this.piecePosition, candidateOffset)
                    || isSeventhColumnExclusion(this.piecePosition, candidateOffset)
                    || isEighthColumnExclusion(this.piecePosition, candidateOffset)) {
                continue;
            }

            // candidate tile is not out of bounds
            if (BoardUtils.isValidTileCoordinate(candidateDestination)) {
                final Tile candidateDestinationTile = board.getTile(candidateDestination);

                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.NormalMove(board, this, candidateDestination));
                } else {
                    final Piece pieceOnDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceOnDestination.getPieceAlliance();

                    // piece on destination tile is enemy
                    if (pieceAlliance != this.pieceAlliance) {
                        legalMoves.add(new Move.CapturingMove(board, this, candidateDestination, pieceOnDestination));
                    }
                }
            }
        }

        return legalMoves;
    }
}
