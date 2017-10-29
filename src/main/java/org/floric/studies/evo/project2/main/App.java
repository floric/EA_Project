package org.floric.studies.evo.project2.main;

import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.solver.Evaluator;
import org.floric.studies.evo.project2.solver.HillClimber;
import org.floric.studies.evo.project2.solver.Mutator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class App {
    public static void main(String[] args) {

        Map<String, Double[]> positions = new HashMap<>();
        Random rnd = new Random();
        for (int i = 0; i < 9; i++) {
            double posX = rnd.nextDouble();
            double posY = rnd.nextDouble();
            positions.put(String.valueOf(i), new Double[]{posX, posY});
        }

        Mutator mutator = new Mutator();
        Evaluator evaluator = new Evaluator(positions);
        HillClimber hillClimber = new HillClimber();
        Solution start = Solution.fromGenotype("012345678012345678012345678");

        Solution bestSolution = hillClimber.climb(100, start, mutator, evaluator);
        System.out.println(bestSolution);
    }
}
