package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;

    public MiniMax() {
        this.boardEvaluator = new StandardBoardEvaluator();
    }

    @Override
    public Move execute(Board board, int depth) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        Player player = board.currentPlayer();

        System.out.println(player + "thinking with depth = " + depth);
        int numMoves = player.getLegalMoves().size();

        for (final Move move : player.getLegalMoves()) {
            final MoveTransition moveTransition = player.makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = player.getAlliance().isWhite() // means black made a move to form the transition board
                        ? min(moveTransition.getTransitionBoard(), depth - 1)
                        : max(moveTransition.getTransitionBoard(), depth - 1);

                if (player.getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (player.getAlliance().isBlack() && currentValue < lowestSeenValue ) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTIme = System.currentTimeMillis() - startTime;
        return bestMove;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    // min is for black, to minimize white's advantage
    public int min(final Board board, final int depth) {
        if (depth == 0 /* or game over */) return this.boardEvaluator.evaluate(board, depth);
        int lowestSeenValue = Integer.MAX_VALUE;
        Player player = board.currentPlayer();
        for (final Move move : player.getLegalMoves()) {
            final MoveTransition moveTransition = player.makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    public int max(final Board board, final int depth) {
        if (depth == 0 /* or game over */) return this.boardEvaluator.evaluate(board, depth);
        int highestSeenValue = Integer.MIN_VALUE;
        Player player = board.currentPlayer();
        for (final Move move : player.getLegalMoves()) {
            final MoveTransition moveTransition = player.makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
}
