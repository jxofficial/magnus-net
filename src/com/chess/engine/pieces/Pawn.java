package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Pawn extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATES = {7, 8, 9, 16};

    public Pawn(Alliance pieceAlliance, int piecePosition) {
        super(pieceAlliance, piecePosition, PieceType.PAWN, true);
    }

    public Pawn(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove) {
        super(alliance, piecePosition, PieceType.PAWN, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition +
                    (this.pieceAlliance.getDirection() * candidateOffset);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) continue;

            if (candidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                // TODO: deal with promotions
                legalMoves.add(new Move.NormalMove(board, this, candidateDestinationCoordinate));
            } else if (candidateOffset == 16 && this.isFirstMove() &&
                    (BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite())) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                    && !board.getTile(candidateDestinationCoordinate).isTileOccupied()
                ) {
                    legalMoves.add(new Move.PawnJumpMove(board, this, candidateDestinationCoordinate));
                }
            } else if (candidateOffset == 7 &&
                    !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                      || BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                    if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                        final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                        if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(new Move.CapturingMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
            } else if (candidateOffset == 9 &&
                    !(BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                    || BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if(this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        legalMoves.add(new Move.PawnCapturingMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                    }
                }
            }
        }

        return legalMoves;
    }


    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }
    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

}
