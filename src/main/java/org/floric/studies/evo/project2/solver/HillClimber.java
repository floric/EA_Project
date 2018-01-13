package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import org.floric.studies.evo.project2.model.Solution;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HillClimber implements ISolver {

    public static final int MAX_ITERATIONS_SINCE_LAST_CHANGE = 400;
    public static final int ITERATIONS_PER_THREAD = 5000;

    public HillClimber() {
    }

    private boolean areEqual(ImmutableList<Integer> a, ImmutableList<Integer> b) {
        if (a.size() != b.size()) {
            return false;
        }

        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }

        return true;
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Mutator mutator = new Mutator();
        Evaluator ev = new Evaluator(positions);

        long startTime = System.currentTimeMillis();
        ImmutableList<Integer> newGen = Solution.generateRandomGenotype(positions.size());
        int lastImprIteration = 0;
        int iteration = 0;
        double bestScore = 0.0;
        ImmutableList<Integer> bestGen = ImmutableList.of();

        // do at least three iterations, otherwise stop if no improvements are done anymore
        while(iteration < lastImprIteration + MAX_ITERATIONS_SINCE_LAST_CHANGE) {
            newGen = getBestSolutionInParallel(newGen, mutator, ev, positions);
            double newScore = ev.evaluate(newGen);
            if (newScore > bestScore) {
                lastImprIteration = iteration;
                bestGen = newGen;
                bestScore = ev.evaluate(newGen);
            }

            System.out.println(String.format("%d, %d since last improvement: %f", iteration, iteration - lastImprIteration, bestScore));

            iteration++;
        }

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Took %f seconds", (endTime - startTime) / 1000.0));

        return Solution.fromGenotype(bestGen);
    }

    private ImmutableList<Integer> getBestSolutionInParallel(ImmutableList<Integer> start, Mutator mutator, Evaluator ev, Map<Integer, Double[]> positions) {
        int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService executorService = Executors.newFixedThreadPool(cores);
        try {
            List<Callable<ImmutableList<Integer>>> tasks = IntStream.range(0, cores)
                    .mapToObj(i -> (Callable<ImmutableList<Integer>>) () -> getBestSolution(positions, mutator, ev, start))
                    .collect(Collectors.toList());

            Optional<ImmutableList<Integer>> futures = executorService
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
                    .sorted(Comparator.comparingDouble(a -> -ev.evaluate(a)))
                    .findFirst();

            ImmutableList<Integer> solution = futures.get();
            executorService.shutdown();
            return solution;
        } catch (InterruptedException e) {
            System.err.println("Execution failed!");
            executorService.shutdown();
            return null;
        }
    }

    private static ImmutableList<Integer> getBestSolution(Map<Integer, Double[]> positions, Mutator mutator, Evaluator ev, ImmutableList<Integer> start) {
        ImmutableList<Integer> bestSolution = ImmutableList.copyOf(start);
        Evaluator evaluator = new Evaluator(positions);
        double bestScore = evaluator.evaluate(bestSolution);

        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
            Mutator.MutationResult newSolution = mutator.mutate(bestSolution);
            double newScore = evaluator.evaluate(newSolution.getIndividuum());
            if (newScore > bestScore) {
                bestScore = newScore;
                bestSolution = newSolution.getIndividuum();
            }
        }

        return bestSolution;
    }
}
