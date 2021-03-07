package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.PawnMove;
import com.chess.engine.board.Move.PawnCapturingMove;
import com.chess.engine.board.Move.PawnJumpMove;
import com.chess.engine.board.Move.PawnEnPassantMove;
import com.chess.engine.board.Move.PawnPromotionMove;

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
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotionMove(
                            new PawnMove(board, this, candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            } else if (candidateOffset == 16 && this.isFirstMove()
                    && ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite())
            )) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                        && !board.getTile(candidateDestinationCoordinate).isTileOccupied()
                ) {
                    legalMoves.add(new PawnJumpMove(board, this, candidateDestinationCoordinate));
                }
            } else if (candidateOffset == 7 &&
                    !(BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                            || BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        // capture and promote
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotionMove(
                                    new PawnCapturingMove(
                                            board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                        } else {
                            legalMoves.add(new PawnCapturingMove(board, this, candidateDestinationCoordinate,
                                    pieceOnCandidate));
                        }
                    }
                    // en passant
                } else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() ==
                            // handle right capture for white, handle right capture for black
                            (this.piecePosition + this.pieceAlliance.getOppositeDirection())) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(
                                    new PawnEnPassantMove(
                                            board,
                                            this,
                                            candidateDestinationCoordinate,
                                            pieceOnCandidate));
                        }
                    }
                }
            } else if (candidateOffset == 9 &&
                    !(BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite()
                            || BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    // capture and promote
                    if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                        legalMoves.add(new PawnPromotionMove(
                                new PawnCapturingMove(
                                        board, this, candidateDestinationCoordinate, pieceOnCandidate)));
                    } else {
                        legalMoves.add(new PawnCapturingMove(board, this, candidateDestinationCoordinate,
                                pieceOnCandidate));
                    }
                }
                // en passant
                else if (board.getEnPassantPawn() != null) {
                    if (board.getEnPassantPawn().getPiecePosition() ==
                            // handle left capture for white, handle left capture for black
                            (this.piecePosition - this.pieceAlliance.getOppositeDirection())) {
                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                            legalMoves.add(
                                    new PawnEnPassantMove(
                                            board,
                                            this,
                                            candidateDestinationCoordinate,
                                            pieceOnCandidate));
                        }
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

    public Piece getPromotionPiece() {
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }

}
