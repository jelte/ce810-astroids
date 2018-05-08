package com.fossgalaxy.games.asteroids.battle.controllers.webpigeon;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

/**
 * Created by jwalto on 12/06/2015.
 */
public class StupidGAWrapper implements BattleController {
    public static final Integer GENOME_LENGTH = 3;
    private double[] genome;

    public StupidGAWrapper(double[] genome) {
        this.genome = genome;
    }

    public Action getMove() {
        return new Action(genome[0], genome[1], genome[2] > 0.5);
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy) {
        return getMove();
    }
}
