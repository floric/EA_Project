package org.floric.studies.evo.project2.main;

import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.solver.HillClimber;
import org.floric.studies.evo.project2.solver.Mutator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class App {

    public static final long POSITIONS_SEED = 123L;

    public static void main(String[] args) {
        Map<Integer, Double[]> positions = new HashMap<>();
        Random rnd = new Random(POSITIONS_SEED);

        List<Integer> genotype = Solution.generateGenotype(27);
        Solution start = Solution.fromGenotype(genotype);

        // generate random positions, can be replaced with real data
        for (int i = 0; i < genotype.size(); i++) {
            int key = genotype.get(i);
            double posX = rnd.nextDouble();
            double posY = rnd.nextDouble();
            positions.put(key, new Double[]{posX, posY});
        }

        // print positions
        for (Map.Entry<Integer, Double[]> entry : positions.entrySet()) {
            System.out.println(String.format("%d: %f, %f", entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }

        Mutator mutator = new Mutator();
        HillClimber hillClimber = new HillClimber();

        Solution bestSolution = hillClimber.climb(start, mutator, positions);
        System.out.println(bestSolution);
    }
}
