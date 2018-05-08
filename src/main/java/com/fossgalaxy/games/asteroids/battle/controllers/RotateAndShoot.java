package com.fossgalaxy.games.asteroids.battle.controllers;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.Ship;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

/**
 * Created by simonlucas on 30/05/15.
 */
public class RotateAndShoot implements BattleController {

    Ship ship;

    Action action;

    public RotateAndShoot() {
        action = new Action();
    }

    public Action action() {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public void setVehicle(Ship ship) {
        // just in case the ship is needed ...
        this.ship = ship;
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy) {
        return new Action(0, 1, true);
    }
}
