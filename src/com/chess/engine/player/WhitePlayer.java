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

public class WhitePlayer extends Player {
    public WhitePlayer(Board board, Collection<Move> whiteStandardLegalMoves, Collection<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    protected Collection<CastleMove> calculateCastleMoves(
            Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves) {
        final List<CastleMove> castleMoves = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            // kingside castle, no pieces in between king and rook
            if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(63);
                // rook has not moved
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // does not pass through check
                    if (Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty()
                            && Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty()) {
                        castleMoves.add(
                                new KingsideCastleMove(
                                        this.board, this.playerKing, 62,
                                        (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            // queenside castle
            } else if (!this.board.getTile(57).isTileOccupied() &&
                        !this.board.getTile(58).isTileOccupied() &&
                        !this.board.getTile(59).isTileOccupied()) {
                final Tile rookTile = this.board.getTile(56);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
                    // check that king does not pass through check
                    if (Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty())
                    castleMoves.add(
                            new QueensideCastleMove(
                                    this.board, this.playerKing, 58,
                                    (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                }
            }
        }

        return ImmutableList.copyOf(castleMoves);
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }
}
