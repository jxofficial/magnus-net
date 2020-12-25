package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    private static final int[] CANDIATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, -15, 17};

    public Knight(final int piecePosition, final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        int candidateDestination;
        List<Move> legalMoves = new ArrayList<>();

        for (final int candidateOffset : CANDIATE_MOVE_COORDINATES) {
            candidateDestination = this.piecePosition + candidateOffset;

            // candidate tile is not out of bounds
            if (true /* isValidTileCoordinate */) {
                final Tile candidateDestinationTile = board.getTile(candidateDestination);

                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move());
                } else {
                    final Piece pieceOnDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceOnDestination.getPieceAlliance();

                    // piece on destination tile is enemy
                    if (pieceAlliance != this.pieceAlliance) {
                        legalMoves.add(new Move());
                    }
                }
            }
        }

        return legalMoves;
    }
}
