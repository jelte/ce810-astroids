package com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy;

import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 19/01/16.
 */
public interface OpponentGenerator
{
    public GAIndividual getOpponent(int numActions);

}
