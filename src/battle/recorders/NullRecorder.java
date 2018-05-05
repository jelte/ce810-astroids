package battle.recorders;

import battle.DataRecorder;
import battle.SimpleBattle;

public class NullRecorder implements DataRecorder {

    @Override
    public void stepUpdate(SimpleBattle simpleBattle) {
        // Do nothing
    }
}
