package battle.controllers.webpigeon;

import battle.Action;
import battle.SimpleBattle;
import ga.Eval2;

/**
 * Created by jwalto on 12/06/2015.
 */
public class GameEvaluator implements Eval2 {
    private SimpleBattle battle;

    public GameEvaluator(SimpleBattle battle, boolean reset) {
        this.battle = battle.clone();

        if (reset) {
            this.battle.reset();
        }
    }

    @Override
    public double pointsDiff(double[] a, double[] b) {
        SimpleBattle currBattle = battle.clone();
        currBattle.reset();

        StupidGAWrapper controller1 = new StupidGAWrapper(a);

        int tick = 0;
        while (!currBattle.isGameOver()) {
            Action action1 = controller1.getMove();

            currBattle.update(action1);
        }

        return currBattle.getPoints();
    }

}
