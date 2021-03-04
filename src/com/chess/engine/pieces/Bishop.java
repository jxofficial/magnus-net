package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop extends Piece {
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    public Bishop(Alliance pieceAlliance, int piecePosition) {
        super(pieceAlliance, piecePosition, PieceType.BISHOP, true);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset)
                        || isEighthColumnExclusion(candidateDestinationCoordinate, candidateOffset)
                )

                    candidateDestinationCoordinate += candidateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new Move.NormalMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance alliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != alliance) {
                            legalMoves.add(new Move.CapturingMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        break; // piece is blocking bishop, no need to consider diagonal further
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }

    /* Private helper methods to ensure piece that exceeds board coordinates
     * is not considered when calculating legal moves
     */
    private static boolean isFirstColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }
}

