package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HillClimber {

    public static final int MAX_ITERATIONS_SINCE_LAST_CHANGE = 500;
    public static final int ITERATIONS_PER_THREAD = 5000;

    public HillClimber() {
    }

    public Solution climb(Solution start, Mutator mutator, Map<Character, Double[]> positions) {
        long startTime = System.currentTimeMillis();
        Solution bestSolution = start.getCopy();
        int lastImprIteration = 0;
        int iteration = 0;
        String bestGen = "";

        // do at least three iterations, otherwise stop if no improvements are done anymore
        while(iteration < lastImprIteration + MAX_ITERATIONS_SINCE_LAST_CHANGE) {
            bestSolution = getBestParallel(bestSolution, mutator, positions);
            if (!bestSolution.getGenotype().equals(bestGen)) {
                lastImprIteration = iteration;
                bestGen = bestSolution.getGenotype();
            }

            System.out.println(String.format("%d, %d since last improvement: %f", iteration, iteration - lastImprIteration, bestSolution.getScore()));
            System.out.println(bestSolution.getGenotype());

            iteration++;
        }

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Took %f seconds", (endTime - startTime) / 1000.0));

        return bestSolution;
    }

    private Solution getBestParallel(Solution start, Mutator mutator, Map<Character, Double[]> positions) {
        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        try {
            List<Callable<Solution>> tasks = IntStream.range(0, cores)
                    .mapToObj(i -> (Callable<Solution>) () -> getBestSolution(positions, mutator, start))
                    .collect(Collectors.toList());

            Optional<Solution> futures = executorService
                    .invokeAll(tasks)
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .sorted(Comparator.comparingDouble(a -> -a.getScore()))
                    .findFirst();

            Solution solution = futures.get();
            executorService.shutdown();
            return solution;
        } catch (InterruptedException e) {
            System.err.println("Execution failed!");
            executorService.shutdown();
            return null;
        }
    }

    private static Solution getBestSolution(Map<Character, Double[]> positions, Mutator mutator, Solution start) {
        Solution bestSolution = start.getCopy();
        Evaluator evaluator = new Evaluator(positions);
        double bestScore = evaluator.evaluate(bestSolution);

        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
            Solution newSolution = mutator.mutate(bestSolution);
            double newScore = evaluator.evaluate(newSolution);
            if (newScore > bestScore) {
                bestScore = newScore;
                bestSolution = newSolution;
                bestSolution.setScore(bestScore);
            }
        }

        return bestSolution;
    }
}
