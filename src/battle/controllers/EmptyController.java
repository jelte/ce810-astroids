package battle.controllers;

import battle.Action;
import battle.BattleController;
import battle.SimpleBattle;

/**
 * Created by davidgundry on 11/06/15.
 */
public class EmptyController implements BattleController {
    @Override
    public Action getAction(SimpleBattle gameStateCopy) {
        return new Action(0, 0, false);
    }
}
