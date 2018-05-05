package battle;

import battle.controllers.mmmcts.MMMCTS;
import battle.recorders.BulletRecorder;
import battle.recorders.MultiRecorder;
import battle.recorders.ScoreRecorder;

import java.util.Arrays;

import static battle.Constants.*;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {

    public static void main(String[] args) {

        // Declare it to the size needed
        int[] params = new int[N_PARAMS];

        // -1 is ignored so fill it with those values
        Arrays.fill(params, -1);

        // Provide value for whatever you want
        params[N_MISSILES] = 200;
        params[N_ASTEROIDS] = 20;
        params[BULLET_TIME_TO_LIVE] = 1000;

        SimpleBattle battle = new SimpleBattle(true, params);

        BattleController player = new MMMCTS();
        ScoreRecorder scoreRecorder = new ScoreRecorder();
        BulletRecorder bulletRecorder = new BulletRecorder();

        battle.playGame(player, new MultiRecorder(scoreRecorder, bulletRecorder));

        System.out.println(scoreRecorder.getScores());
        System.out.println(bulletRecorder.getBullets());
    }

}
