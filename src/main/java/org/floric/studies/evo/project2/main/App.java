package org.floric.studies.evo.project2.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.solver.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class App {

    public static final long POSITIONS_SEED = 123L;
    public static final int TEAMS_COUNT = 18;
    public static final int CANDIDATES_TO_EVALUATE = 1000000;

    public static void main(String[] args) {
        List<ISolver> solvers = Lists.newLinkedList();
        // solvers.add(new HillClimber(CANDIDATES_TO_EVALUATE));
        solvers.add(new EvolutionarySolver(CANDIDATES_TO_EVALUATE));
        // solvers.add(new BruteForceSolver());

        // start server to show evolutionary results
        /*try {
            ProcessBuilder bld = new ProcessBuilder("yarn", "start");
            bld.directory(new File("./results-spa/app"));
            bld.start();
        } catch (IOException e) {
            System.out.println("Starting display server failed!");
        }*/

        Map<Integer, Double[]> positions = generatePositions(TEAMS_COUNT, POSITIONS_SEED);
        Evaluator evaluator = new Evaluator(positions);

        System.out.println(printPositions(positions));

        // run solvers
        for (ISolver solver: solvers) {
            System.out.println(String.format("Running %s...\n", solver.getName()));

            // run
            long startTime = System.currentTimeMillis();
            Solution solution = solver.solve(positions);
            long endTime = System.currentTimeMillis();

            // show result
            double totalDistance = evaluator.getTotalDistance(solution.getTeams());
            double score = evaluator.evaluate(solution);
            double calculationSeconds = (endTime - startTime) / 1000.0;

            System.out.println(String.format("Running time: %f seconds\nScore: %f\nDistance: %f\nSolution:\n%s\nGenotype:\n%s", calculationSeconds, score, totalDistance, solution, solution.getGenotype()));
        }
    }

    public static String printPositions(Map<Integer, Double[]> positions) {
        StringBuilder strBld = new StringBuilder();
        strBld.append("Positions:\n");
        for (Map.Entry<Integer, Double[]> entry : positions.entrySet()) {
            strBld.append(String.format("%d: %f, %f\n", entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
        }
        return strBld.toString();
    }

    public static Map<Integer, Double[]> generatePositions(int count, long seed) {
        Map<Integer, Double[]> positions = new HashMap<>();
        Random rnd = new Random(seed);

        ImmutableList<Integer> genotype = Solution.generateGenotype(count);

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
