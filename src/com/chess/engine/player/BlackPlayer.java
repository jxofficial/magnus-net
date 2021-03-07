package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.CastleMove;
import com.chess.engine.board.Move.KingsideCastleMove;
import com.chess.engine.board.Move.QueensideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    @Override
    public Collection<CastleMove> calculateCastleMoves(
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
                        castleMoves.add(new KingsideCastleMove(
                                this.board, this.playerKing, 6,
                                (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
                // queenside castle
            } else if (!this.board.getTile(1).isTileOccupied() &&
                    !this.board.getTile(2).isTileOccupied() &&
                    !this.board.getTile(3).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // check that king does not pass through check
                    if (Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty()
                            && Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty()) {

                        castleMoves.add(new QueensideCastleMove(
                                this.board, this.playerKing, 2,
                                (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                    }
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
