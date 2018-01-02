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

public class EvolutionarySolver {

    private static final double SIMULATED_ANNEALING_SPEED = 0.9997;
    private static final int MUTATION_WEIGHT_INFLUENCE = 4;
    private static final double ANNEALING_BORDER_DROP_FACTOR = 0.95;
    private static final double ANNEALING_BORDER_MIN_SCORE = 0.3;

    private Map<String, Integer> improvements = Maps.newHashMap();
    private Map<String, Double> mutationWeights = Maps.newHashMap();
    private ExportResult result;
    private long totalImprovements = 0L;

    public EvolutionarySolver() {
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);
        int individualsCount = 80;
        int iterations = 20000;
        double bestValue = Double.MIN_VALUE;
        double minScore = 0.0;
        double scoreFraction = ANNEALING_BORDER_MIN_SCORE;
        int bestValueIteration = 0;
        ImmutableList<Integer> bestIndividuum = ImmutableList.of();
        result = new ExportResult();

        // start
        List<ImmutableList<Integer>> individuals = Lists.newArrayList();
        for (int i = 0; i < individualsCount; i++) {
            ImmutableList<Integer> initialPermutation = Solution.generateRandomGenotype(positions.size());
            individuals.add(initialPermutation);
        }

        initMutationProbabilities();

        // loop
        for (int i = 0; i < iterations; i++) {
            // evaluate
            List<Double> evaluations = Lists.newArrayList();
            List<Double> scores = Lists.newArrayList();
            double currentMax = Double.MIN_VALUE;
            for (int j = 0; j < individuals.size(); j++) {
                ImmutableList<Integer> individuum = individuals.get(j);
                double score = ev.evaluate(Solution.fromGenotype(individuum));
                scores.add(score);

                if (score > bestValue) {
                    bestValue = score;
                    bestIndividuum = individuum;
                    bestValueIteration = i;
                    scoreFraction = minScore * ANNEALING_BORDER_DROP_FACTOR / bestValue;
                    Solution s = Solution.fromGenotype(bestIndividuum);
                    System.out.println(String.format("New Score: %f, distance: %f", bestValue, ev.getTotalDistance(s.getTeams())));
                    result.getSolutions().put(i, bestIndividuum);
                    result.setBestIndividuum(bestIndividuum);
                }
                if (score > currentMax) {
                    currentMax = score;
                }
            }

            minScore = bestValue * (scoreFraction + (1.0 - scoreFraction) * (1 - Math.pow(SIMULATED_ANNEALING_SPEED, i - bestValueIteration)));
            result.getMinScore().add(minScore);

            for (int j = 0; j < individuals.size(); j++) {
                double score = scores.get(j);
                evaluations.add(Math.max(0.0, score - minScore));
            }

            // normalize scores for selection
            double minEvalutations = evaluations.stream().mapToDouble(val -> val).min().orElse(0.0);
            double sumEvalutations = evaluations.stream().mapToDouble(val -> val - minEvalutations).sum();

            double maxScores = scores.stream().mapToDouble(val -> val).max().orElse(1.0);
            double avgScores = scores.stream().mapToDouble(val -> val).average().orElse(0.0);
            result.getAvgScore().add(avgScores);

            for (int j = 0; j < evaluations.size(); j++) {
                Double val = evaluations.get(j);
                double fraction = sumEvalutations == 0.0 ? 0.0 : ((val - minEvalutations) / sumEvalutations);
                evaluations.set(j, fraction);
            }

            // if border of annealing is hit, use best individuum to continue
            Set<Integer> bestIndividuums = Sets.newHashSet();
            if (sumEvalutations == 0.0) {
                for (int j = 0; j < scores.size(); j++) {
                    if (scores.get(j) == maxScores) {
                        bestIndividuums.add(j);
                    }
                }
            }
            for (Integer index : bestIndividuums) {
                evaluations.set(index, 1.0 / bestIndividuums.size());
            }

            // select
            List<ImmutableList<Integer>> tmp = ImmutableList.copyOf(individuals);
            individuals.clear();
            for (int j = 0; j < individualsCount; j++) {
                int selectIndex = select(evaluations);
                individuals.add(tmp.get(selectIndex));
            }

            // mutate
            tmp = ImmutableList.copyOf(individuals);
            individuals.clear();
            for (ImmutableList<Integer> individuum : tmp) {
                individuals.add(mutate(individuum, ev));
            }

            // calculate ratio of individuums with score higher then annealing border
            long validIndividuums = evaluations.stream().filter(val -> val > 0.0).count();
            result.getValidIndividuumsRatio().add((double) validIndividuums / evaluations.size());

            // modify mutation weights
            if (i % 100 == 0) {
                modifyMutationWeights();
                System.out.println(String.format("%s: min: %f, best: %f; valid individuums: %d", i, minScore, bestValue, validIndividuums));
            }
            // export progress to file
            if (i % 500 == 0) {
                exportProgress();
            }
            result.getScore().add(bestValue);
        }

        printSolution(bestIndividuum, ev);

        return null;
    }

    private void modifyMutationWeights() {
        mutationWeights.forEach((key, value) -> {
            double newWeight = ((MUTATION_WEIGHT_INFLUENCE - 1.0) * value + getImprovementsCount(key) / (double) totalImprovements) / MUTATION_WEIGHT_INFLUENCE;
            mutationWeights.put(key, newWeight);
        });
    }

    private void printSolution(ImmutableList<Integer> bestIndividuum, Evaluator ev) {
        Solution s = Solution.fromGenotype(bestIndividuum);
        System.out.println(String.format("Solution:\n%s", s));
        System.out.println(String.format("Distance: %f", ev.getTotalDistance(s.getTeams())));
        System.out.println(bestIndividuum);
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

    private int select(List<Double> evaluations) {
        Random rnd = new Random();
        double val = rnd.nextDouble();
        double currentSum = evaluations.get(0);

        for (int k = 0; k < evaluations.size() - 1; k++) {
            if (val < currentSum) {
                return k;
            }
            currentSum += evaluations.get(k + 1);
        }

        return evaluations.size() - 1;
    }

    private ImmutableList<Integer> mutate(ImmutableList<Integer> individuum, Evaluator ev) {
        Random rnd = new Random();
        double mutationVal = rnd.nextDouble();

        double oldScore = ev.evaluate(Solution.fromGenotype(individuum));
        String mutationType = "";
        double cyclicSwapPro = mutationWeights.get("cyclicSwap");
        double changeCookPro = mutationWeights.get("changeCook");

        if (mutationVal < cyclicSwapPro) {
            for (int i = 0; i < rnd.nextInt(3) + 1; i++) {
                individuum = Mutator.cyclicSwap(individuum);
            }
            mutationType = "cyclicSwap";
        } else if (mutationVal < changeCookPro + cyclicSwapPro) {
            for (int i = 0; i < rnd.nextInt(2) + 1; i++) {
                individuum = Mutator.changeCook(individuum);
            }
            mutationType = "changeCook";
        } else {
            individuum = Mutator.swapGuests(individuum);
            mutationType = "swapGuests";
        }

        double newScore = ev.evaluate(Solution.fromGenotype(individuum));
        if (newScore > oldScore) {
            int improvementsForType = getImprovementsCount(mutationType) + 1;
            improvements.put(mutationType, improvementsForType);
            totalImprovements++;
        }

        return individuum;
    }

    private int getImprovementsCount(String mutationType) {
        return improvements.containsKey(mutationType) ? improvements.get(mutationType) : 0;
    }
}
