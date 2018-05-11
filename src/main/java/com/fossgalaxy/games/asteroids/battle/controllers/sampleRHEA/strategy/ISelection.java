package com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy;

import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ISelection
{
    GAIndividual getParent(GAIndividual[] pop, GAIndividual first);
}
