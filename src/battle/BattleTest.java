package battle;

import battle.controllers.EmptyController;
import battle.controllers.FireForwardController;
import battle.controllers.Human.WASDController;
import battle.controllers.Naz.Naz_AI;
import battle.controllers.mmmcts.MMMCTS;
import battle.controllers.webpigeon.StaticEvolver;
import battle.controllers.webpigeon.StupidGAWrapper;

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
