package battle;

import battle.controllers.mmmcts.MMMCTS;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {

    public static void main(String[] args) {

        SimpleBattle battle = new SimpleBattle();

        BattleController player1 = new MMMCTS();
        battle.playGame(player1);
    }

}
