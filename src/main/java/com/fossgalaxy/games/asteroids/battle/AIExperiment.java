package com.fossgalaxy.games.asteroids.battle;

import com.fossgalaxy.games.asteroids.battle.controllers.Naz.Naz_AI;
import com.fossgalaxy.games.asteroids.battle.controllers.Piers.PiersMCTS;
import com.fossgalaxy.games.asteroids.battle.controllers.RotateAndShoot;
import com.fossgalaxy.games.asteroids.battle.controllers.mmmcts.MMMCTS;
import com.fossgalaxy.games.asteroids.battle.recorders.ScoreRecorder;

import java.util.*;
import java.util.function.Supplier;

import static com.fossgalaxy.games.asteroids.battle.Constants.N_PARAMS;

public class AIExperiment {

    public static final int NUM_GAMES_PER_CONTROLLER = 5;

    public static void main(String[] args) {
        List<Supplier<BattleController>> controllerFunctions = Arrays.asList(
                PiersMCTS::new,
                MMMCTS::new,
                RotateAndShoot::new,
                Naz_AI::new
        );

        Map<String, List<Integer>> scores = new HashMap<>();

        // Declare it to the size needed
        int[] params = new int[N_PARAMS];

        // -1 is ignored so fill it with those values
        Arrays.fill(params, -1);

        SimpleBattle battle = new SimpleBattle(false, params);

        for (Supplier<BattleController> controllerFunction : controllerFunctions) {
            String name = controllerFunction.get().getClass().getSimpleName();
            scores.put(name, new ArrayList<>());
            for (int i = 0; i < NUM_GAMES_PER_CONTROLLER; i++) {
                System.out.println("Playing game: " + name + " : " + i);
                ScoreRecorder scoreRecorder = new ScoreRecorder();
                battle.playGame(controllerFunction.get(), scoreRecorder);
                int finalScore = scoreRecorder.getFinalScore();
                System.out.println("Finished game: " + name + " : " + i + " Scored: " + finalScore);
                scores.get(name).add(finalScore);
            }
        }

        for (Map.Entry<String, List<Integer>> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " Scored: " + entry.getValue().stream().mapToInt(Integer::new).sum() / NUM_GAMES_PER_CONTROLLER);
        }
    }
}
