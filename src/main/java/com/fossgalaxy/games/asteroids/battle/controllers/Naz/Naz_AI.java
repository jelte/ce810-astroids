package com.fossgalaxy.games.asteroids.battle.controllers.Naz;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

/**
 * Created by avkokk on 11/06/2015.
 */
public class Naz_AI implements BattleController {

    public Naz_AI() {

    }


    public Action getAction(SimpleBattle game) {
        Action action = new Action();
        // Work out what we want to do
        action.shoot = true; //don't forget to set to true  ///The ship will always shoot because it's free and it's simple enough
        //  TODO
        action.thrust = 1;
        action.turn = 20;


        // Set the "action" variable
        //getTicks possibly


        if (action.shoot == true) {
            //action.turn = 3;
            //action.thrust shall be either 0 or 1
            // random value will increment/vary in randomly
            action.thrust = Math.random() * 10;
        }
        //  TODO

        // Return the action variable to the game

        return action;
    }


}
