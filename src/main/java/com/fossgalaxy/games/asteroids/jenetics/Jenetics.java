package com.fossgalaxy.games.asteroids.jenetics;

import com.fossgalaxy.games.asteroids.battle.AIExperiment;
import com.fossgalaxy.games.asteroids.battle.BattleController;
import com.fossgalaxy.games.asteroids.battle.SimpleBattle;
import com.fossgalaxy.games.asteroids.battle.controllers.Naz.Naz_AI;
import com.fossgalaxy.games.asteroids.battle.controllers.Piers.PiersMCTS;
import com.fossgalaxy.games.asteroids.battle.controllers.RotateAndShoot;
import com.fossgalaxy.games.asteroids.battle.controllers.mmmcts.MMMCTS;
import org.jenetics.*;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.util.Factory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fossgalaxy.games.asteroids.battle.Constants.*;

public class Jenetics {

    private static final boolean[] USING = new boolean[N_PARAMS];

    static {
        Arrays.fill(USING, false);
            USING[N_MISSILES] = true;
            USING[BULLET_TIME_TO_LIVE] = true;
            USING[SHIP_MAX_SPEED] = true;
            USING[SHIP_STEER_RATE] = true;
            USING[BULLET_KILL_SHIP] = true;
    };

    private static final List<Supplier<BattleController>> controllerFunctions = Arrays.asList(
            PiersMCTS::new,
//            MMMCTS::new,
            RotateAndShoot::new
//            Naz_AI::new
    );

    public static void main(String[] args) {

        // Min and max for each parameter that is being used
        int[][] limits = {
                {10, 500}, // N_MISSILES
                {20, 100}, // BULLETT_TIME_TO_LIVE
                {1, 10},
                {5, 50},
                {0, 1}
        };

        // Convert limits to the chromosomes
        List<Chromosome<IntegerGene>> genes = Arrays.stream(limits)
                .map(x -> IntegerChromosome.of(x[0], x[1], 1))
                .collect(Collectors.toList());

        // Chromosomes to genotype
        Factory<Genotype<IntegerGene>> genotype = Genotype.of(genes);


        ExecutorService exec = Executors.newSingleThreadExecutor();
        final Engine<IntegerGene, Double> engine = Engine
                .builder(Jenetics::fitness, genotype)
                .populationSize(2)
                .executor(exec)
                .optimize(Optimize.MAXIMUM)
                .build();

        final Genotype<IntegerGene> result = engine.stream()
                .limit(limit.byExecutionTime(Duration.ofMinutes(120)))
                .limit(300)
                .peek( x-> {
                    System.out.println("Generation: " + x.getGeneration());
                    System.out.println("Best Fitness: " + x.getBestFitness());
                })
                .collect(EvolutionResult.toBestGenotype());

        System.out.println(result);
        exec.shutdown();
    }

    private static double fitness(final Genotype<IntegerGene> genotype){

        int[] params = getParamsFromGenotype(genotype);
        AIExperiment experiment = new AIExperiment(5, controllerFunctions, params);
        Map<String, List<Integer>> scores = experiment.run();
        Map<String, Integer> averages = new HashMap<>();
        for(Map.Entry<String, List<Integer>> entry : scores.entrySet()){
            averages.put(entry.getKey(), entry.getValue().stream().mapToInt(Integer::new).sum() / 5);
        }

        // Calculate fitness of this?
        return averages.get("PiersMCTS") - averages.get("RotateAndShoot");
    }

    private static int[] getParamsFromGenotype(Genotype<IntegerGene> genotype){
        int[] params = new int[N_PARAMS];
        Arrays.fill(params, -1);

        int chromosomeCount = 0;
        for(int index = 0; index < N_PARAMS; index++){
            if(!USING[index]) continue;
            params[index] = genotype.get(chromosomeCount++, 0).intValue();
        }

        return params;
    }
}
