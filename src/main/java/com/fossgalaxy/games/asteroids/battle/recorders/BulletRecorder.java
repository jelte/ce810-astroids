package com.fossgalaxy.games.asteroids.battle.recorders;

import com.fossgalaxy.games.asteroids.battle.DataRecorder;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

import java.util.ArrayList;
import java.util.List;

public class BulletRecorder implements DataRecorder {

    private final List<Integer> bullets = new ArrayList<>();

    @Override
    public void stepUpdate(SimpleBattle simpleBattle) {
        bullets.add(simpleBattle.getMissilesLeft());
    }

    public List<Integer> getBullets() {
        return bullets;
    }
}
