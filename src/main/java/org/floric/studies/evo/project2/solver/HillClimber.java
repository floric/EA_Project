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

import static org.floric.studies.evo.project2.solver.EvolutionarySolver.printProgress;

public class HillClimber implements ISolver {

    public int candidates = 0;
    public static final int CHILDREN_COUNT = 500;

    public HillClimber(int candidates) {
        this.candidates = candidates;
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Mutator mutator = new Mutator();
        Evaluator ev = new Evaluator(positions);
        int cores = Runtime.getRuntime().availableProcessors();

        ImmutableList<Integer> newGen = Solution.generateRandomGenotype(positions.size());
        int i = 0;
        double bestScore = 0.0;
        int iterations = candidates / (CHILDREN_COUNT * cores);
        ImmutableList<Integer> bestGen = newGen;

        System.out.println(String.format("%d iterations and %d children; %d candidates", iterations, CHILDREN_COUNT, iterations * CHILDREN_COUNT * cores));

        // do at least three iterations, otherwise stop if no improvements are done anymore
        while(i < iterations) {
            newGen = getBestSolutionInParallel(newGen, mutator, ev, positions);
            double newScore = ev.evaluate(newGen);
            if (newScore > bestScore) {
                bestGen = newGen;
                bestScore = newScore;
            }

            i++;
            printProgress(i, iterations);
        }

        return Solution.fromGenotype(bestGen);
    }

    @Override
    public String getName() {
        return "Hillclimber";
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

        for (int i = 0; i < CHILDREN_COUNT; i++) {
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
