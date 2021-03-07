package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.CastleMove;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    public Player(final Board board,
                  final Collection<Move> legalMoves,
                  final Collection<Move> opponentLegalMoves) {

        this.board = board;
        this.playerKing = establishKing();
        // legal moves + castle moves
        this.legalMoves = ImmutableList.copyOf(
                Iterables.concat(legalMoves, calculateCastleMoves(legalMoves, opponentLegalMoves)));
        this.isInCheck = !Player
                .calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegalMoves)
                .isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    // protected allows for access by subclass in all packages & all files in same package
    // default allows only for files in same package
    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isCheckmated() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isStalemated() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        // we make a move here to produce a transition board, assume it's a white move
        // a transition board is a possibly valid board that reflects the move that has been made
        final Board transitionBoard = move.execute();
        final int opponentKingPosition = transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition();
        final Collection<Move> playerLegalMoves = transitionBoard.currentPlayer().getLegalMoves();

        // hence, here we are seeing from the black POV
        // since white has already made a move, we check all of black's moves, and see if any move attacks the white king
        final Collection<Move> attacksOnPlayerKing = Player.calculateAttacksOnTile(opponentKingPosition, playerLegalMoves);

        if (!attacksOnPlayerKing.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract Collection<CastleMove> calculateCastleMoves(
            Collection<Move> playerLegalMoves,
            Collection<Move> opponentLegalMoves);


    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("Invalid board. King does not exist");
    }

    public King getPlayerKing() {
        return playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }
}