package com.fossgalaxy.games.asteroids.battle.controllers.mmmcts;


import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;
import com.fossgalaxy.games.asteroids.battle.controllers.mmmcts.tools.ElapsedCpuTimer;
import com.fossgalaxy.games.asteroids.battle.controllers.mmmcts.tools.Utils;

import java.util.Random;

public class SingleTreeNode {
    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE = 10000000.0;
    public static Action DEFAULTACTION = new Action(1, 0, true);
    public static double epsilon = 1e-6;
    public static double egreedyEpsilon = 0.05;
    public static Random m_rnd;
    public static SimpleBattle OGState;
    public static int SCORE_BONUS = 300;
    public static int MISSILES_LEFT_MOD = 10;
    public static int DISTANCE_MOD = 12;
    public static int GRAVITATE_DISTANCE = 40;
    public static boolean GRAVITATE = false;
    protected static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    public SimpleBattle state;
    public SingleTreeNode parent;
    public SingleTreeNode[] children;
    //public int playerId;
    public double totValue;
    public int nVisits;
    private int m_depth;

    public SingleTreeNode(Random rnd) {
        this(null, null, rnd);
    }

    public SingleTreeNode(SimpleBattle state, SingleTreeNode parent, Random rnd) {
        this.state = state;

        OGState = state;

        this.parent = parent;
        this.m_rnd = rnd;
        children = new SingleTreeNode[MMMCTS.NUM_ACTIONS];
        totValue = 0.0;
        if (parent != null)
            m_depth = parent.m_depth + 1;
        else
            m_depth = 0;
    }

    public void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        //playerId = playerID;
        double avgTimeTaken = 0;
        double acumTimeTaken = 0;
        long remaining = elapsedTimer.remainingTimeMillis();
        //long remaining = 15;
        int numIters = 0;

        int remainingLimit = 5;
        while (remaining > 2 * avgTimeTaken && remaining > remainingLimit) {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            SingleTreeNode selected = treePolicy();
            double delta = selected.rollOut();
            backUp(selected, delta);

            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis());

            avgTimeTaken = acumTimeTaken / numIters;
            remaining = elapsedTimer.remainingTimeMillis();
            //System.out.println(elapsedTimerIteration.elapsedMillis() + " --> " + acumTimeTaken + " (" + remaining + ")");
        }
        //System.out.println("-- " + numIters + " -- ( " + avgTimeTaken + ")");
    }

    public SingleTreeNode treePolicy() {

        SingleTreeNode cur = this;

        while (!cur.state.isGameOver() && cur.m_depth < MMMCTS.ROLLOUT_DEPTH) {
            if (cur.notFullyExpanded()) {
                return cur.expand();

            } else {
                SingleTreeNode next = cur.uct();
                //SingleTreeNode next = cur.egreedy();
                cur = next;
            }
        }

        return cur;
    }

    public SingleTreeNode expand() {

        int bestAction = 0;
        double bestValue = -1;

        for (int i = 0; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
                bestAction = i;
                bestValue = x;
            }
        }

        SimpleBattle nextState = state.clone();
        nextState.update(MMMCTS.actions.get(bestAction).buildAction());


        SingleTreeNode tn = new SingleTreeNode(nextState, this, this.m_rnd);
        children[bestAction] = tn;
        return tn;

    }

    public SingleTreeNode uct() {

        SingleTreeNode selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (SingleTreeNode child : this.children) {
            double hvVal = child.totValue;
            double childValue = hvVal / (child.nVisits + this.epsilon);


            childValue = Utils.normalise(childValue, bounds[0], bounds[1]);

            double uctValue = childValue +
                    MMMCTS.K * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + this.epsilon));

            // small sampleRandom numbers: break ties in unexpanded nodes
            uctValue = Utils.noise(uctValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly

            // small sampleRandom numbers: break ties in unexpanded nodes
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }

        if (selected == null) {
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + this.children.length);
        }

        return selected;
    }

    public SingleTreeNode egreedy() {


        SingleTreeNode selected = null;

        if (m_rnd.nextDouble() < egreedyEpsilon) {
            //Choose randomly
            int selectedIdx = m_rnd.nextInt(children.length);
            selected = this.children[selectedIdx];

        } else {
            //pick the best Q.
            double bestValue = -Double.MAX_VALUE;
            for (SingleTreeNode child : this.children) {
                double hvVal = child.totValue;
                hvVal = Utils.noise(hvVal, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                // small sampleRandom numbers: break ties in unexpanded nodes
                if (hvVal > bestValue) {
                    selected = child;
                    bestValue = hvVal;
                }
            }

        }


        if (selected == null) {
            throw new RuntimeException("Warning! returning null: " + this.children.length);
        }

        return selected;
    }

    public double rollOut() {
        SimpleBattle rollerState = state.clone();
        int thisDepth = this.m_depth;

        while (!finishRollout(rollerState, thisDepth)) {

            int action = m_rnd.nextInt(MMMCTS.NUM_ACTIONS);

            rollerState.update(MMMCTS.actions.get(action).buildAction());

            thisDepth++;
        }

        double delta = value(rollerState);

        if (delta < bounds[0])
            bounds[0] = delta;

        if (delta > bounds[1])
            bounds[1] = delta;

        return delta;
    }

    public double value(SimpleBattle a_gameState) {

        //double score = OGState.getPoints(playerId);
        double score = 5000;

        if (OGState.getPoints() < a_gameState.getPoints()) {
            score += HUGE_NEGATIVE;
        }

        if (a_gameState.getPoints() > OGState.getPoints()) {
            //score += HUGE_POSITIVE;
            score += SCORE_BONUS;
        }

        int mleft = Math.abs(a_gameState.getMissilesLeft());

        if (mleft > 0) {
            //score -= 2000 * mused;
            score += MISSILES_LEFT_MOD * mleft;
        }

        //double alpha = 180 + Math.atan2((s1.s.y - s2.s.y), (s1.s.x - s2.s.x));
        //score += 10 * alpha;


        //System.out.println("SCORE - " + score);

        return score;
    }

    public boolean finishRollout(SimpleBattle rollerState, int depth) {
        if (depth >= MMMCTS.ROLLOUT_DEPTH)      //rollout end condition.
            return true;

        if (rollerState.isGameOver())               //end of game
            return true;

        return false;
    }

    public void backUp(SingleTreeNode node, double result) {
        SingleTreeNode n = node;
        while (n != null) {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }


    public int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;

        for (int i = 0; i < children.length; i++) {

            if (children[i] != null) {
                if (first == -1)
                    first = children[i].nVisits;
                else if (first != children[i].nVisits) {
                    allEqual = false;
                }

                double childValue = children[i].nVisits;
                childValue = Utils.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1) {
            System.out.println("Unexpected selection!");
            selected = 0;
        } else if (allEqual) {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        }
        return selected;
    }

    public int bestAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;

        for (int i = 0; i < children.length; i++) {

            if (children[i] != null) {
                double childValue = children[i].totValue / (children[i].nVisits + this.epsilon);
                childValue = Utils.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1) {
            System.out.println("Unexpected selection!");
            selected = 0;
        }

        return selected;
    }


    public boolean notFullyExpanded() {
        for (SingleTreeNode tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }
}
