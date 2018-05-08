package com.fossgalaxy.games.asteroids.battle.recorders;

import com.fossgalaxy.games.asteroids.battle.DataRecorder;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

public class MultiRecorder implements DataRecorder {

    private final DataRecorder[] recorders;

    public MultiRecorder(DataRecorder... recorders) {
        this.recorders = recorders;
    }

    @Override
    public void stepUpdate(SimpleBattle simpleBattle) {
        for (DataRecorder recorder : recorders) {
            recorder.stepUpdate(simpleBattle);
        }
    }
}
