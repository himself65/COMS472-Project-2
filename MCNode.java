package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * @author Zeyu Yang
 */

public class MCNode {
    private int player;
    private int enemy;
    private double wins;
    private double playouts;
    private CheckersMove moveTaken;
    private CheckersData state;
    private ArrayList<MCNode> children;
    private MCNode parent;

    public MCNode(int player, int enemy, int wins, int playouts, CheckersData state, CheckersMove moveTaken) {
        this.player = player;
        this.enemy = enemy;
        this.wins = wins;
        this.playouts = playouts;
        this.state = state;
        this.moveTaken = moveTaken;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public int getPlayer() {
        return player;
    }

    public int getEnemy() {
        return enemy;
    }

    public double getWins() {
        return wins;
    }

    public double getPlayouts() {
        return playouts;
    }

    public CheckersMove getMoveTaken() {
        return moveTaken;
    }

    public CheckersData getState() {
        return state;
    }

    public MCNode getParent() {
        return parent;
    }

    public ArrayList<MCNode> getChildren() {
        return children;
    }

    public void addWin() {
        wins++;
    }

    public void addPlayout() {
        playouts++;
    }

    public void addChild(MCNode child) {
        children.add(child);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
