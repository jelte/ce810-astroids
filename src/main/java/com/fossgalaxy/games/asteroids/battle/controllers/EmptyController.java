package com.fossgalaxy.games.asteroids.battle.controllers;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

/**
 * Created by davidgundry on 11/06/15.
 */
public class EmptyController implements BattleController {
    @Override
    public Action getAction(SimpleBattle gameStateCopy) {
        return new Action(0, 0, false);
    }
}
