package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece {
    private static final int[] CANDIDATE_MOVE_COORDINATES = {8, 16};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
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
                legalMoves.add(new Move.PieceMove(board, this, candidateDestinationCoordinate));
            } else if (candidateOffset == 16 && this.isFirstMove() &&
                    (BoardUtils.SECOND_ROW[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.getPieceAlliance().isWhite())) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()
                    && !board.getTile(candidateDestinationCoordinate).isTileOccupied()
                ) {
                    legalMoves.add(new PieceMove(board, this, candidateDestinationCoordinate));
                }
            }
        }

        return null; 
    }
}
