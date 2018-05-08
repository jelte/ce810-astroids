package com.fossgalaxy.games.asteroids.battle.controllers.Piers;

import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

/**
 * Created by pwillic on 12/06/2015.
 */
public class PiersBattleTest {

    public static void main(String[] args) {
        SimpleBattle battle = new SimpleBattle();
        BattleController player1 = new PiersMCTS();
        battle.playGame(player1);
    }
}
