package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.battle.controllers.mmmcts.MMMCTS;
import com.fossgalaxy.games.asteroids.battle.controllers.onesteplookahead.OneStepLookAhead;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleOLMCTS.Agent;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleOLMCTS.SingleMCTSPlayer;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.BattleEvoController;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.search.GASearch;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy.PMutation;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy.TournamentSelection;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.strategy.UniformCrossover;
import com.fossgalaxy.games.asteroids.battle.recorders.BulletRecorder;
import com.fossgalaxy.games.asteroids.battle.recorders.MultiRecorder;
import com.fossgalaxy.games.asteroids.battle.recorders.ScoreRecorder;

import java.util.Arrays;
import java.util.Random;

import static com.fossgalaxy.games.asteroids.battle.Constants.*;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {

    public static final boolean SHOW_ROLLOUTS = false;

    public static void main(String[] args) {

        // Declare it to the size needed
        int[] params = new int[N_PARAMS];

        // -1 is ignored so fill it with those values
        Arrays.fill(params, -1);

        // Provide value for whatever you want
        params[N_MISSILES] = 200;
        params[N_ASTEROIDS] = 20;
        params[BULLET_TIME_TO_LIVE] = 50;
        params[BULLET_KILL_SHIP] = 1;

        SimpleBattle battle = new SimpleBattle(true, params);

        Random bob = new Random();

        /*BattleController player = new BattleEvoController(new GASearch(
                new UniformCrossover(bob),
                new PMutation(bob),
                new TournamentSelection(bob),
                bob
                ));*/
        BattleController player = new SingleMCTSPlayer(bob);

        ScoreRecorder scoreRecorder = new ScoreRecorder();
        BulletRecorder bulletRecorder = new BulletRecorder();

        battle.playGame(player, new MultiRecorder(scoreRecorder, bulletRecorder));

        System.out.println(scoreRecorder.getScores());
        System.out.println(bulletRecorder.getBullets());
    }

}
