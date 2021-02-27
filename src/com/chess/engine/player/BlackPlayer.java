package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    protected Collection<Move> calculateKingCastles(
            Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {
        final List<Move.CastleMove> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // kingside castle, no pieces in between king and rook
            if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                // rook has not moved
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // does not pass through check
                    if (Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty()
                            && Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty()) {
                        // TODO: add new KingsideCastleMove
                        castleMoves.add(null);
                    }
                }
                // queenside castle
            } else if (!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // TODO: check that king does not pass through check
                    // TODO: add new QueensideCastleMove
                    castleMoves.add(null);
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }
}
