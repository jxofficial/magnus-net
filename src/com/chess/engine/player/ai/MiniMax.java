package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.Player;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;

    public MiniMax() {
        this.boardEvaluator = null;
    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
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
