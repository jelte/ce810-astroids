package battle.recorders;

import battle.DataRecorder;
import battle.SimpleBattle;

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
}
