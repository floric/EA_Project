package org.floric.studies.evo.project2.main;

import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.solver.HillClimber;
import org.floric.studies.evo.project2.solver.Mutator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class App {

    public static final long POSITIONS_SEED = 123L;

    public static void main(String[] args) {
        Map<String, Double[]> positions = new HashMap<>();
        Random rnd = new Random(POSITIONS_SEED);

        String genotype = Solution.generateGenotype(9);
        Solution start = Solution.fromGenotype(genotype);

        // generate random positions, can be replaced with real data
        for (int i = 0; i < genotype.length(); i++) {
            String key = genotype.substring(i, i + 1);
            double posX = rnd.nextDouble();
            double posY = rnd.nextDouble();
            positions.put(key, new Double[]{posX, posY});
        }

        Mutator mutator = new Mutator();
        HillClimber hillClimber = new HillClimber();

        Solution bestSolution = hillClimber.climb(100, start, mutator, positions);
        System.out.println(bestSolution);
    }
}
