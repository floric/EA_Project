package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.floric.studies.evo.project2.io.ExportResult;
import org.floric.studies.evo.project2.model.Solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EvolutionarySolver implements ISolver {

    private static final double INDIVIDUALS_COUNT_MULTIPLY = 2;
    private static final int SELECT_COUNT = 1;
    private static final int MUTATION_WEIGHT_INFLUENCE = 5;

    private static final int ITERATIONS_UNTIL_MUTATION_WEIGHTS_ADJUST = 200;
    private static final int ITERATIONS_TO_EXPORT_RESULT = 2000;

    private static final double MUTATION_MIN_SUCCESS_RATE = 0.001;

    private Map<String, Long> improvements = Maps.newHashMap();
    private Map<String, Long> failures = Maps.newHashMap();

    private Map<String, Double> mutationWeights = Maps.newHashMap();
    private ExportResult result;
    private int candidates = 0;

    public EvolutionarySolver(int candidates) {
        this.candidates = candidates;
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);
        double bestValue = Double.MIN_VALUE;
        int individualsCount = (int) (positions.size() * INDIVIDUALS_COUNT_MULTIPLY);
        int iterations = candidates / individualsCount;

        ImmutableList<Integer> bestIndividuum = ImmutableList.of();
        result = new ExportResult();

        // start
        result.setIndividualsCount(individualsCount);
        result.setPositions(positions);
        List<ImmutableList<Integer>> individuals = initIndividuums(individualsCount, positions);
        initMutationProbabilities();

        System.out.println(String.format("%d iterations; %d individuals with %d selections; %d candidates", iterations, individualsCount, SELECT_COUNT, individualsCount * iterations));

        // loop
        for (int i = 0; i < iterations; i++) {
            // evaluate
            List<Double> scores = Lists.newArrayList();

            // check individuals, if they are better then existing best one
            for (int j = 0; j < individuals.size(); j++) {
                ImmutableList<Integer> individuum = individuals.get(j);
                double score = ev.evaluate(Solution.fromGenotype(individuum));
                scores.add(score);

                if (score > bestValue) {
                    bestValue = score;
                    bestIndividuum = individuum;
                    result.getSolutions().put(i, bestIndividuum);
                    result.setBestIndividuum(bestIndividuum);
                }
            }

            // select
            individuals = selectBestN(scores, individuals, SELECT_COUNT);

            // mutate
            List<ImmutableList<Integer>> tmp = ImmutableList.copyOf(individuals);
            individuals.clear();
            Random rnd = new Random();
            for (int j = 0; j < individualsCount; j++) {
                int individuumIndex = rnd.nextInt(tmp.size());
                ImmutableList<Integer> mutated = mutate(tmp.get(individuumIndex), ev);
                individuals.add(mutated);
            }

            // modify mutation weights
            if (i % ITERATIONS_UNTIL_MUTATION_WEIGHTS_ADJUST == 0 && i != 0) {
                modifyMutationWeights();
            }
            // export progress to file
            if (i % ITERATIONS_TO_EXPORT_RESULT == 0) {
                exportProgress();
            }
            result.getScore().add(bestValue);

            printProgress(i, iterations);
        }

        exportProgress();

        return Solution.fromGenotype(bestIndividuum);
    }

    @Override
    public String getName() {
        return "Evolutionary Algorithm";
    }

    private void modifyMutationWeights() {
        double successRateSum = mutationWeights.entrySet().stream().mapToDouble(entry -> {
            long improvements = getImprovementsCount(entry.getKey());
            long failures = getFailuresCount(entry.getKey());
            return getSuccessRate(improvements, failures);
        }).sum();

        mutationWeights.forEach((key, value) -> {
            long improvements = getImprovementsCount(key);
            long failures = getFailuresCount(key);
            double successRate = getSuccessRate(improvements, failures);
            double newWeight = ((MUTATION_WEIGHT_INFLUENCE - 1.0) * value + (successRate / successRateSum)) / MUTATION_WEIGHT_INFLUENCE;
            mutationWeights.put(key, newWeight);
        });
        resetSuccessCount();

        System.out.println(mutationWeights.entrySet().stream().map(val -> String.format("%s -> %f; ", val.getKey(), val.getValue())).collect(Collectors.joining()));
    }

    private double getSuccessRate(long improvements, long failures) {
        return Math.max(MUTATION_MIN_SUCCESS_RATE, (double) improvements / (failures + improvements));
    }

    private void exportProgress() {
        Gson gson = new Gson();
        String output = gson.toJson(result);
        try {
            Files.write(Paths.get("./results-spa/app/src/result.json"), output.getBytes());
        } catch (IOException e) {
            System.out.println("Write exception!");
        }
    }

    private void initMutationProbabilities() {
        mutationWeights.put("cyclicSwap", 1.0 / 3);
        mutationWeights.put("changeCook", 1.0 / 3);
        mutationWeights.put("swapGuests", 1.0 / 3);
    }

    private List<ImmutableList<Integer>> initIndividuums(int count, Map<Integer, Double[]> positions) {
        List<ImmutableList<Integer>> individuals = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            ImmutableList<Integer> initialPermutation = Solution.generateRandomGenotype(positions.size());
            individuals.add(initialPermutation);
        }
        return individuals;
    }

    private List<ImmutableList<Integer>> selectBestN(List<Double> scores, List<ImmutableList<Integer>> individuals, int n) {
        List<ImmutableList<Integer>> bestIndividuals = Lists.newArrayList();
        Set<Integer> bestUsedIndices = Sets.newHashSet();

        for (int i = 0; i < n; i++) {
            double maxScore = Double.MIN_VALUE;
            int maxIndex = 0;

            for (int j = 0; j < individuals.size(); j++) {
                double curScore = scores.get(j);
                if (curScore > maxScore && !bestUsedIndices.contains(j)) {
                    maxScore = curScore;
                    maxIndex = j;
                }
            }

            bestIndividuals.add(individuals.get(maxIndex));
            bestUsedIndices.add(maxIndex);
        }

        return bestIndividuals;
    }


    private ImmutableList<Integer> mutate(ImmutableList<Integer> individuum, Evaluator ev) {
        Mutator m = new Mutator();
        Mutator.MutationResult result = m.mutate(individuum, mutationWeights);

        double oldScore = ev.evaluate(Solution.fromGenotype(individuum));
        double newScore = ev.evaluate(Solution.fromGenotype(result.getIndividuum()));

        if (newScore > oldScore) {
            long improvementsForType = getImprovementsCount(result.getMutationType()) + 1;
            improvements.put(result.getMutationType(), improvementsForType);
        } else {
            long failuresForType = getFailuresCount(result.getMutationType()) + 1;
            failures.put(result.getMutationType(), failuresForType);
        }

        return result.getIndividuum();
    }

    private long getImprovementsCount(String mutationType) {
        return improvements.containsKey(mutationType) ? improvements.get(mutationType) : 0;
    }

    private long getFailuresCount(String mutationType) {
        return failures.containsKey(mutationType) ? failures.get(mutationType) : 0;
    }

    private void resetSuccessCount() {
        improvements.forEach((name, val) -> improvements.put(name, 0l));
        failures.forEach((name, val) -> failures.put(name, 0l));
    }

    public static void printProgress(int i, int maxI) {
        int step = maxI / 20;
        if (i % step == 0) {
            System.out.println(String.format("%d/%d -> %d percent", i, maxI, 100 * i / maxI));
        }
    }
}
