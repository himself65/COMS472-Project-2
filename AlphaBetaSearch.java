package edu.iastate.cs472.proj2;

/**
 * @author Zeyu Yang
 *
 * Alpha-Beta Search Algorithm
 */
public class AlphaBetaSearch extends AdversarialSearch {
    /**
     * Constants for the alpha-beta search algorithm.
     */
    private static final int SEARCH_DEPTH_LIMIT = 8;
    private static final int KING_VALUE = 3;
    private static final int NORMAL_PIECE_VALUE = 1;
    private static final double WIN_SCORE = 1.0;
    private static final double LOSS_SCORE = -1.0;
    private static final double INFINITY = Double.POSITIVE_INFINITY;

    private final CheckersData[] nextStates = new CheckersData[SEARCH_DEPTH_LIMIT + 1];

    /**
     * Make a move based on the current state of the game.
     * @param legalMoves The legal moves for the current state.
     * @return The move to make.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        if (legalMoves == null || legalMoves.length == 0) {
            return null;
        }
        if (legalMoves.length == 1) {
            return legalMoves[0];
        }

        int bestMoveIndex = findBestMoveIndex(legalMoves);
        return legalMoves[bestMoveIndex];
    }

    /**
     * Find the best move based on the current state of the game.
     * @param legalMoves The legal moves for the current state.
     * @return The index of the best move.
     */
    private int findBestMoveIndex(CheckersMove[] legalMoves) {
        int bestMoveIndex = 0;
        double alpha = -INFINITY;
        double beta = INFINITY;
        double bestValue = -INFINITY;

        nextStates[0] = board;
        for (int i = 0; i < legalMoves.length; i++) {
            CheckersData clonedData = new CheckersData(nextStates[0]);
            clonedData.makeMove(legalMoves[i]);
            nextStates[1] = clonedData;

            double value = minValue(nextStates[1].getLegalMoves(CheckersData.RED), 1, alpha, beta);
            if (value > bestValue) {
                bestMoveIndex = i;
                bestValue = value;
            }
            if (bestValue >= beta) {
                break;
            }
            alpha = Math.max(alpha, bestValue);
        }

        return bestMoveIndex;
    }

    /**
     * Max-value function for alpha-beta search.
     * @param legalMoves The legal moves for the current state.
     * @param depth The current depth of the search.
     * @param alpha The current alpha value.
     * @param beta The current beta value.
     * @return The maximum value.
     */
    private double maxValue(CheckersMove[] legalMoves, int depth, double alpha, double beta) {
        if (isTerminalState(legalMoves)) {
            return evaluateState(nextStates[depth]);
        }

        double value = -INFINITY;
        for (CheckersMove move : legalMoves) {
            CheckersData clonedData = new CheckersData(nextStates[depth]);
            clonedData.makeMove(move);
            nextStates[depth + 1] = clonedData;

            value = Math.max(value, minValue(nextStates[depth + 1].getLegalMoves(CheckersData.RED), depth + 1, alpha, beta));
            if (value >= beta) {
                return value;
            }
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    /**
     * Min-value function for alpha-beta search.
     * @param legalMoves The legal moves for the current state.
     * @param depth The current depth of the search.
     * @param alpha The current alpha value.
     * @param beta The current beta value.
     * @return The minimum value.
     */
    private double minValue(CheckersMove[] legalMoves, int depth, double alpha, double beta) {
        if (isTerminalState(legalMoves)) {
            return evaluateState(nextStates[depth]);
        }

        double value = INFINITY;
        for (CheckersMove move : legalMoves) {
            CheckersData clonedData = new CheckersData(nextStates[depth]);
            clonedData.makeMove(move);
            nextStates[depth + 1] = clonedData;

            value = Math.min(value, maxValue(nextStates[depth + 1].getLegalMoves(CheckersData.BLACK), depth + 1, alpha, beta));
            if (value <= alpha) {
                return value;
            }
            beta = Math.min(beta, value);
        }
        return value;
    }

    /**
     * Check if the current state is a terminal state.
     * @param legalMoves The legal moves for the current state.
     * @return True if the current state is a terminal state, false otherwise.
     */
    private boolean isTerminalState(CheckersMove[] legalMoves) {
        return legalMoves == null || nextStates[SEARCH_DEPTH_LIMIT] != null;
    }

    /**
     * Evaluate the current state of the game.
     * @param state The current state of the game.
     * @return The utility value of the state.
     */
    private double evaluateState(CheckersData state) {
        if (state == null) {
            return LOSS_SCORE; // If no state is available, assume a loss.
        }

        int blackScore = 0;
        int redScore = 0;
        for (int row = 0; row < CheckersData.ROWS; row++) {
            for (int col = (row % 2); col < CheckersData.COLS; col += 2) {
                switch (state.pieceAt(row, col)) {
                    case CheckersData.RED:
                        redScore += NORMAL_PIECE_VALUE;
                        break;
                    case CheckersData.BLACK:
                        blackScore += NORMAL_PIECE_VALUE;
                        break;
                    case CheckersData.RED_KING:
                        redScore += KING_VALUE;
                        break;
                    case CheckersData.BLACK_KING:
                        blackScore += KING_VALUE;
                        break;
                }
            }
        }
        return calculateUtility(blackScore, redScore);
    }

    /**
     * Calculate the utility value of the current state.
     * @param blackScore blackScore
     * @param redScore redScore
     * @return The utility value of the current state.
     */
    private double calculateUtility(int blackScore, int redScore) {
        if (redScore == 0) {
            return WIN_SCORE; // Black wins
        }
        if (blackScore == 0) {
            return LOSS_SCORE; // Red wins
        }
        return (double) (blackScore - redScore) / (blackScore + redScore);
    }
}
