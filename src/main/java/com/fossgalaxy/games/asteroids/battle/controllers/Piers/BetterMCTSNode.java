package com.fossgalaxy.games.asteroids.battle.controllers.Piers;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

import java.util.Random;

/**
 * Created by Piers on 12/06/2015.
 */
public class BetterMCTSNode {
    private static final double EPSILON = 1e-6;

    private Random random = new Random();
    private Action ourMoveToThisState;

    private BetterMCTSNode parent;
    private BetterMCTSNode[] children;
    private int numberOfChildrenExpanded;
    private boolean ourNode;

    private int currentDepth;

    private double totalValue = 0;
    private int numberOfVisits = 1;

    private double explorationConstant;
    private PiersMCTS mcts;

    public BetterMCTSNode(double explorationConstant, PiersMCTS mcts) {
        this.explorationConstant = explorationConstant;
        currentDepth = 0;
        ourNode = true;
        children = new BetterMCTSNode[mcts.getAllActions().length];
        this.mcts = mcts;
    }

    private BetterMCTSNode(BetterMCTSNode parent, Action ourMoveToThisState) {
        this.explorationConstant = parent.explorationConstant;
        this.ourMoveToThisState = ourMoveToThisState;
        this.parent = parent;
        this.mcts = parent.mcts;
        this.children = new BetterMCTSNode[mcts.getAllActions().length];
        this.currentDepth = parent.currentDepth + 1;
        this.ourNode = parent.ourNode;
    }



    public BetterMCTSNode select(SimpleBattle state, int maxDepth) {
        BetterMCTSNode current = this;
        while (current.currentDepth <= maxDepth) {
            if (current.fullyExpanded()) {
                current = current.selectBestChild();
                for (int i = 0; i < PiersMCTS.ACTIONS_PER_MACRO; i++) {
                    state.update(current.ourMoveToThisState);
                }
            } else {
                return current.expand(state);
            }
        }
        return current;
    }

    public BetterMCTSNode expand(SimpleBattle state) {

        // Calculate the possible action spaces
        // can we shoot

        children = new BetterMCTSNode[mcts.getAllActions().length];

        int childToExpand = random.nextInt(mcts.getAllActions().length);
        while (children[childToExpand] != null) {
            childToExpand = random.nextInt(mcts.getAllActions().length);
        }
        children[childToExpand] = new BetterMCTSNode(this, mcts.getAllActions()[childToExpand]);
        for (int i = 0; i < PiersMCTS.ACTIONS_PER_MACRO; i++) {
            state.update(mcts.getAllActions()[childToExpand]);
        }
        numberOfChildrenExpanded++;
        return children[childToExpand];
    }

    public BetterMCTSNode selectBestChild() {
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                double score = children[i].calculateChild();
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }
        }
        return children[bestIndex];
    }

    public double calculateChild() {
        return (totalValue / numberOfVisits) +
                (explorationConstant * (Math.sqrt(Math.log(parent.numberOfVisits) / numberOfVisits)));
    }

    public void updateValues(double value) {
        BetterMCTSNode current = this;
        double alteredValue = value / 1000;
        double discountFactor = 0.95;
        while (current.parent != null) {
            current.numberOfVisits++;
            alteredValue *= discountFactor;
            current.totalValue += (alteredValue);
            current = current.parent;
        }
        current.totalValue += alteredValue;
        current.numberOfVisits++;
    }

    public double rollout(SimpleBattle state, int maxDepth) {
        int currentRolloutDepth = this.currentDepth;
        while (maxDepth > currentRolloutDepth && !state.isGameOver()) {
            Action first = mcts.getAllActions()[random.nextInt(mcts.getAllActions().length)];
            for (int i = 0; i < PiersMCTS.ACTIONS_PER_MACRO; i++) {
                state.update(first);
            }
            currentRolloutDepth++;
        }
        return evaluateState(state);
    }

    private double evaluateState(SimpleBattle state) {
        if (state.isGameOver()) {
            return state.getShip().isDead() ? -10000 : 10000;
        }

        int missilesUsed = state.getMissilesLeft();
        int ourPoints = state.getPoints();
        return (ourPoints + (missilesUsed * 5));
    }

    public Action getBestAction() {
        double bestScore = -Double.MAX_VALUE;
        int bestIndex = -1;
        if (children == null) return mcts.getAllActions()[0];

        for (int i = 0; i < children.length; i++) {
            if (children[i] != null) {
                double childScore = children[i].totalValue + (random.nextFloat() * EPSILON);
                if (childScore > bestScore) {
                    bestScore = childScore;
                    bestIndex = i;
                }
            }
        }
        if (bestIndex == -1) return mcts.getAllActions()[0];
        return children[bestIndex].ourMoveToThisState;
    }

    private boolean fullyExpanded() {
        return numberOfChildrenExpanded == mcts.getAllActions().length;
    }

    public void printAllChildren() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < children.length; i++) {
            builder.append("Value: ");
            builder.append(children[i].totalValue / children[i].numberOfVisits);
            builder.append(" Action: ");
            builder.append(children[i].ourMoveToThisState);
            builder.append("UCB: ");
            builder.append(children[i].calculateChild());
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }


}
