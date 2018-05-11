package com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy;

import com.fossgalaxy.games.asteroids.battle.SimpleBattle;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public abstract class ICoevPairing
{
    public static int GROUP_SIZE = 3;

    public abstract double evaluate(SimpleBattle game, GAIndividual individual, GAIndividual[] otherPop);

    public int getGroupSize() {
        return GROUP_SIZE;
    }

}
