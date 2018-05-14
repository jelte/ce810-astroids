package com.fossgalaxy.games.asteroids.battle.controllers.sampleOLMCTS;

import java.awt.*;
import java.util.Random;

import com.fossgalaxy.games.asteroids.battle.Action;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;
import com.fossgalaxy.games.asteroids.battle.controllers.sampleRHEA.ActionMap;
import com.fossgalaxy.games.asteroids.utilities.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 07/11/13
 * Time: 17:13
 *
 * ATTENTION: This file is edited to adapt to BattleGame.                       
 * Edited by Jialin Liu, University of Essex                                    
 * Date: 01/04/2016 
 */
public class SingleMCTSPlayer implements BattleController
{

    public static SingleMCTSPlayer magicalLambdaOfPlusOneMCTSing() {
        return new SingleMCTSPlayer(new Random());
    }


    /**
     * Root of the tree.
     */
    public SingleTreeNode m_root;

    /**
     * Random generator.
     */
    public Random m_rnd;

    public int playerID;


    public SingleMCTSPlayer(Random a_rnd)
    {
        m_rnd = a_rnd;
    }

    /**
     * Inits the tree with the new observation state in the root.
     * @param a_gameState current state of the game.
     */
    public void init(SimpleBattle a_gameState, int _playerID)
    {
        //Set the game observation to a newly root node.
        //System.out.println("learning_style = " + learning_style);
        m_root = new SingleTreeNode(m_rnd,playerID);
        m_root.rootState = a_gameState;
        playerID = _playerID; 
    }

    /**
     * Runs MCTS to decide the action to take. It does not reset the tree.
     * @param elapsedTimer Timer when the action returned is due.
     * @return the action to execute in the game.
     */
    public int run(ElapsedCpuTimer elapsedTimer)
    {
        //Do the search within the available time.
        m_root.mctsSearch(elapsedTimer);

        //Determine the best action to take and return it.
        int action = m_root.mostVisitedAction();
        //int action = m_root.bestAction();
        return action;
    }
    
    public int run(SimpleBattle gameState, int playerId, ElapsedCpuTimer elapsedTimer)
    {
        init(gameState,playerId);
        m_root.mctsSearch(elapsedTimer);
        int action = m_root.mostVisitedAction();  
        //int action = m_root.bestAction();
        return action; 
    }

    @Override                                                                   
    public Action getAction(SimpleBattle gameState)
    {
        ElapsedCpuTimer hack = new ElapsedCpuTimer();
        hack.setMaxTimeMillis(40);

        int best_action = run(gameState, 0, hack);
        return ActionMap.ActionMap[best_action];
    }

    public void draw(Graphics2D g)
    {

    }
}