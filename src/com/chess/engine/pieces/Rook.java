package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Piece {
    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};

    public Rook(Alliance pieceAlliance, int piecePosition) {
        super(pieceAlliance, piecePosition, PieceType.ROOK, true);
    }

    public Rook(Alliance pieceAlliance, int piecePosition, final boolean isFirstMove) {
        super(pieceAlliance, piecePosition, PieceType.ROOK, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;

            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(candidateDestinationCoordinate, candidateOffset)
                        || isEighthColumnExclusion(candidateDestinationCoordinate, candidateOffset)
                ) {
                    break;
                }

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
                        break; // piece is blocking rook, no need to consider diagonal further
                    }
                }
            }
        }

        return legalMoves;
    }



    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }

    private static boolean isFirstColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1);
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
