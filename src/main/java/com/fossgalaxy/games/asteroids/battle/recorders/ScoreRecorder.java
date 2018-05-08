package com.fossgalaxy.games.asteroids.battle.recorders;

import com.fossgalaxy.games.asteroids.battle.DataRecorder;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;

import java.util.ArrayList;
import java.util.List;

public class ScoreRecorder implements DataRecorder {

    private final List<Integer> scores = new ArrayList<>();

    @Override
    public void stepUpdate(SimpleBattle simpleBattle) {
        scores.add(simpleBattle.getPoints());
    }

    public List<Integer> getScores() {
        return scores;
    }

    public int getFinalScore() {
        return scores.get(scores.size() - 1);
    }
}
