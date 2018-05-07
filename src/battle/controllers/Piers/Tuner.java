package battle.controllers.Piers;

import battle.SimpleBattle;
import battle.recorders.ScoreRecorder;

import java.util.*;

public class Tuner {

    public static void main(String[] args) {
        SimpleBattle battle = new SimpleBattle(false);



        Map<Integer, List<Integer>> scores = new TreeMap<>();

        int[] lengths = {1, 2, 3, 5, 10, 15, 25, 50};

        for(int i = 0; i < lengths.length; i++){
            int macroLength = lengths[i];
            scores.put(macroLength, new ArrayList<>());
            for(int game = 0; game < 20; game++){
                System.out.println("Playing game " + macroLength + " " + game);
                ScoreRecorder scoreRecorder = new ScoreRecorder();
                battle.playGame(new PiersMCTS(macroLength), scoreRecorder);
                int finalScore = scoreRecorder.getFinalScore();
                scores.get(macroLength).add(finalScore);
            }
        }

        for(Map.Entry<Integer, List<Integer>> entry : scores.entrySet()){
            System.out.println(entry.getKey() + " Scored: " + entry.getValue().stream().mapToInt(Integer::new).sum() / 20);
        }
    }
}
