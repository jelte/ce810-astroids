package battle.recorders;

import battle.DataRecorder;
import battle.SimpleBattle;

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
