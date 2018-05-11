package com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA;

import com.fossgalaxy.games.asteroids.battle.Action;

public class ActionMap {

    public final static Action[] actions = new Action[] {
            new Action(0.0,0.0,false),
            new Action(0.0,-1.0,false),
            new Action(0.0,1.0,false),
            new Action(1.0,0.0,false),
            new Action(1.0,-1.0,false),
            new Action(1.0,1.0,false),
            new Action(0.0,0.0,true),
            new Action(-1.0,0.0,false),
            new Action(-1.0,1.0,false),
            new Action(-1.0,-1.0,false)
    };

    //compatability with diego's code.
    public static final Action[] ActionMap = actions;


        public static int mutateThrust(int action)
    {
        if (action==0||action==6) return 3;
        if (action==1) return 4;
        if (action==2) return 5;
        if (action==3 || action==7) return 0;
        if (action==4 || action==9) return 1;
        if (action==5 || action==8) return 2;
        return -1;
    }

    public static int mutateSteer(int action, boolean rightwise)
    {
        //From a side to center.
        if (action==1 || action==2) return 0;
        if (action==4) return 3;
        if (action==5) return 3;
        if (action==8 || action==9) return 7;

        if (action==0||action==6)
            if (rightwise) return 2;
            else return 1;

        if (action==3 || action==7)
            if (rightwise) return 5;
            else return 4;

        return -1;
    }

    /**
     * Shooting
     * @param action
     * @return action index after mutation
     */
    public static int mutateShooting(int action)
    {
        if(action == 6)
            return 0;
        else
            return 6;
    }

}
