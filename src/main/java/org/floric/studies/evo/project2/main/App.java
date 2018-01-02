package org.floric.studies.evo.project2.main;

import com.google.common.collect.ImmutableList;
import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.solver.EvolutionarySolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class App {

    public static final long POSITIONS_SEED = 123L;
    public static final int TEAMS_COUNT = 54;

    public static void main(String[] args) {
        Map<Integer, Double[]> positions;

        ImmutableList<Integer> genotype = Solution.generateGenotype(TEAMS_COUNT);
        Solution start = Solution.fromGenotype(genotype);

        positions = generatePositions(TEAMS_COUNT, POSITIONS_SEED);

        // print positions
        for (Map.Entry<Integer, Double[]> entry : positions.entrySet()) {
            System.out.println(String.format("%d: %f, %f", entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }

        EvolutionarySolver solver = new EvolutionarySolver();
        solver.solve(positions);

        // HillClimber hillClimber = new HillClimber();
        // Solution bestSolution = hillClimber.climb(start, positions);
    }

    public static Map<Integer, Double[]> generatePositions(int count, long seed) {
        Map<Integer, Double[]> positions = new HashMap<>();
        Random rnd = new Random(seed);

        ImmutableList<Integer> genotype = Solution.generateGenotype(count);
        Solution start = Solution.fromGenotype(genotype);

        // generate random positions, can be replaced with real data
        for (int i = 0; i < genotype.size(); i++) {
            int key = genotype.get(i);
            double posX = rnd.nextDouble();
            double posY = rnd.nextDouble();
            positions.put(key, new Double[]{posX, posY});
        }

        return positions;
    }
}
