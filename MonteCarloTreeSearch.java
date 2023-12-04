package edu.iastate.cs472.proj2;

import java.util.Random;

/**
 * @author Zeyu Yang
 * Monte Carlo Tree Search
 */
public class MonteCarloTreeSearch extends AdversarialSearch {
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final int SIMULATION_COUNT = 1000;
    private static final int STEPS_TO_DRAW = 40;
    private static final Random random = new Random();

    /**
     * Make a move using Monte Carlo Tree Search
     * @param legalMoves Legal moves for the current player
     * @return The best move found
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        MCNode root = new MCNode(CheckersData.RED, CheckersData.BLACK, 0, 0, this.board, null);

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            MCNode node = selectNode(root);
            if (!isTerminal(node)) {
                node = expandNode(node);
            }
            String result = simulateRandomPlayout(node);
            backpropagate(node, result);
        }

        MCNode bestChild = root.getChildren().stream()
                .max((child1, child2) -> Double.compare(child1.getPlayouts(), child2.getPlayouts()))
                .orElseThrow(IllegalStateException::new);

        return bestChild.getMoveTaken();
    }

    /**
     * Select a node to expand
     * @param node The node to select from
     * @return The selected node
     */
    private MCNode selectNode(MCNode node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream()
                    .max((child1, child2) -> Double.compare(ucbValue(child1), ucbValue(child2)))
                    .orElseThrow(IllegalStateException::new);
        }
        return node;
    }

    /**
     * Expand a node by adding a child node
     * @param node The node to expand
     * @return The child node
     */
    private MCNode expandNode(MCNode node) {
        CheckersMove[] legalMoves = node.getState().getLegalMoves(node.getEnemy());
        if (legalMoves == null) {
            return node; // Node represents a terminal state
        }
        CheckersMove move = legalMoves[random.nextInt(legalMoves.length)];
        CheckersData nextState = new CheckersData(node.getState());
        nextState.makeMove(move);
        MCNode childNode = new MCNode(node.getEnemy(), node.getPlayer(), 0, 0, nextState, move);
        node.addChild(childNode);
        return childNode;
    }

    /**
     * Simulate a random playout from a node
     * @param node The node to simulate from
     * @return The result of the playout
     */
    private String simulateRandomPlayout(MCNode node) {
        CheckersData state = new CheckersData(node.getState());
        int currentPlayer = node.getEnemy(); // Enemy plays first
        int stepsWithoutCapture = STEPS_TO_DRAW;

        while (!isTerminal(state, currentPlayer)) {
            CheckersMove[] legalMoves = state.getLegalMoves(currentPlayer);
            CheckersMove move = legalMoves[random.nextInt(legalMoves.length)];
            state.makeMove(move);

            if (state.numberOfPieces() == node.getState().numberOfPieces()) {
                if (--stepsWithoutCapture == 0) {
                    return "DRAW";
                }
            } else {
                stepsWithoutCapture = STEPS_TO_DRAW; // Reset the draw counter
            }

            currentPlayer = (currentPlayer == CheckersData.RED) ? CheckersData.BLACK : CheckersData.RED;
        }

        return (currentPlayer == node.getPlayer()) ? "LOSE" : "WIN";
    }

    /**
     * Backpropagate the result of a playout
     * @param node The node to backpropagate from
     * @param result The result of the playout
     */
    private void backpropagate(MCNode node, String result) {
        while (node != null) {
            node.addPlayout();
            if (node.getPlayer() == CheckersData.RED && "WIN".equals(result)) {
                node.addWin();
            } else if (node.getPlayer() == CheckersData.BLACK && "LOSE".equals(result)) {
                node.addWin();
            }
            node = node.getParent();
        }
    }

    /**
     * Calculate the UCB value of a node
     * @param node The node to calculate the UCB value of
     * @return The UCB value of the node
     */
    private double ucbValue(MCNode node) {
        if (node.getPlayouts() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        MCNode parentNode = node.getParent();
        double winRate = node.getWins() / node.getPlayouts();
        double explorationTerm = EXPLORATION_CONSTANT * Math.sqrt(Math.log(parentNode.getPlayouts()) / node.getPlayouts());
        return winRate + explorationTerm;
    }

    /**
     * Check if a node is terminal
     * @param node The node to check
     * @return True if the node is terminal, false otherwise
     */
    private boolean isTerminal(MCNode node) {
        return isTerminal(node.getState(), node.getPlayer());
    }

    /**
     * Check if a state is terminal
     * @param state The state to check
     * @param player The player to check for
     * @return True if the state is terminal, false otherwise
     */
    private boolean isTerminal(CheckersData state, int player) {
        return state.getLegalMoves(player) == null;
    }
}
