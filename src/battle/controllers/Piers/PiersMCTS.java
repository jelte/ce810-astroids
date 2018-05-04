package battle.controllers.Piers;

import asteroids.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by pwillic on 11/06/2015.
 */
public class PiersMCTS implements BattleController {

    protected static int ACTIONS_PER_MACRO = 15;

    private MacroAction currentBestAction = new MacroAction(new Action(1, 0, false));
    private BetterMCTSNode root;

    public PiersMCTS() {
        BetterMCTSNode.setAllActions();
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy) {
        GameTimer timer = new GameTimer();
        timer.setTimeBudgetMilliseconds(25);
        Action action = currentBestAction.getAction();


        if (root == null) root = new BetterMCTSNode(2.0,  this);
        if (currentBestAction.getTimesUsed() >= ACTIONS_PER_MACRO) root = new BetterMCTSNode(2.0, this);

        int i = 0;
        while (timer.remainingTimePercent() > 10) {
            SimpleBattle copy = gameStateCopy.clone();
            BetterMCTSNode travel = root.select(copy, 3);
            double result = travel.rollout(copy, 10);
            travel.updateValues(result);
            i++;
        }

        System.out.println("Rollouts achieved: " + i);

        if (currentBestAction.getTimesUsed() >= ACTIONS_PER_MACRO) {
            currentBestAction = new MacroAction(root.getBestAction());
        }
        return action;
    }


}

class MacroAction {
    private Action action;
    private int timesUsed;
    private Action nonShootingVersion;

    public MacroAction(Action action) {
        this.action = action;
        nonShootingVersion = new Action(action.thrust, action.turn, false);
        timesUsed = 0;
    }

    /**
     * returns the action and increments the number of times it has been used
     *
     * @return
     */
    public Action getAction() {
        timesUsed++;
        return (timesUsed <= 2) ? action : nonShootingVersion;
    }

    public int getTimesUsed() {
        return timesUsed;
    }
}
