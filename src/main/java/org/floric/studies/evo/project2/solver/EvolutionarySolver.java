package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.floric.studies.evo.project2.model.Solution;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EvolutionarySolver {

    public static final double SIMULATED_ANNEALING_SPEED = 0.999;

    private Map<String, Integer> improvements = Maps.newHashMap();
    private Map<String, Double> mutationProbability = Maps.newHashMap();
    private long totalImprovements = 0L;
    private long triedMutations = 0L;

    public EvolutionarySolver() {
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);
        int individualsCount = 200;
        int iterations = 10000;
        double bestValue = Double.MIN_VALUE;
        List<Integer> bestIndividuum = Lists.newArrayList();

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
                }
                if (score > currentMax) {
                    currentMax = score;
                }
            }

            double minScore = bestValue * (0.99 + 0.01 * (1 - Math.pow(SIMULATED_ANNEALING_SPEED, i)));

            for (int j = 0; j < individuals.size(); j++) {
                double score = scores.get(j);
                evaluations.add(Math.max(0.0, score - minScore));
            }

            // normalize scores for selection
            double minEvalutations = evaluations.stream().mapToDouble(val -> val).min().orElse(0.0);
            double maxEvaluations = evaluations.stream().mapToDouble(val -> val).max().orElse(1.0);
            double sumEvalutations = evaluations.stream().mapToDouble(val -> val - minEvalutations).sum();
            double avgEvaluations = evaluations.stream().mapToDouble(val -> val).average().orElse(0.0);
            double varianceEvalutations = evaluations.stream().mapToDouble(val -> Math.pow((val - avgEvaluations), 2.0)).sum() / evaluations.size();

            double minScores = scores.stream().mapToDouble(val -> val).min().orElse(0.0);
            double maxScores = scores.stream().mapToDouble(val -> val).max().orElse(1.0);
            double avgScores = scores.stream().mapToDouble(val -> val).average().orElse(0.0);
            double varianceScores = scores.stream().mapToDouble(val -> Math.pow((val - avgScores), 2.0)).sum() / scores.size();

            // System.out.println(String.format("Avg: %f, Var: %f, Min: %f, Max: %f|%f, Sum: %f, Minscore: %f", avgEvaluations, varianceEvalutations, minEvalutations, maxEvaluations, bestValue, sumEvalutations, minScore));

            for (int j = 0; j < evaluations.size(); j++) {
                Double val = evaluations.get(j);
                double fraction = sumEvalutations == 0.0 ? 0.0 : ((val - minEvalutations) / sumEvalutations);
                evaluations.set(j, fraction);
            }

            // if border of annealing is hit, use best individuum to continue
            if (sumEvalutations == 0.0) {
                for (int j = 0; j < scores.size(); j++) {
                    if (scores.get(j) == maxScores) {
                        evaluations.set(j, 1.0);
                    }
                }
            }

            if (i % 100 == 0) {
                System.out.println(evaluations.stream().map(val -> val * 100.0).collect(Collectors.toList()));
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

            if (i % 100 == 0) {
                mutationProbability.entrySet().forEach(entry -> {
                    double newProbability = (3 * entry.getValue() + getImprovementsCount(entry.getKey()) / (double) totalImprovements) / 4.0;
                    mutationProbability.put(entry.getKey(), newProbability);
                });
                System.out.println(String.format("%s: min: %f, best: %f, success: %f", i, minScore, bestValue,(double) totalImprovements * 100.0 / triedMutations));
            }
        }

        return null;
    }

    private void initMutationProbabilities() {
        mutationProbability.put("cyclicSwap", 1.0 / 3);
        mutationProbability.put("changeCook", 1.0 / 3);
        mutationProbability.put("swapGuests", 1.0 / 3);
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
        double cyclicSwapPro = mutationProbability.get("cyclicSwap");
        double changeCookPro = mutationProbability.get("changeCook");
        double swapGuestsPro = mutationProbability.get("swapGuests");

        if (mutationVal < cyclicSwapPro) {
            for (int i = 0; i < rnd.nextInt(9) + 1; i++) {
                individuum = Mutator.cyclicSwap(individuum);
            }
            mutationType = "cyclicSwap";
        } else if (mutationVal < changeCookPro + cyclicSwapPro) {
            for (int i = 0; i < 2; i++) {
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

        triedMutations++;

        return individuum;
    }

    private int getImprovementsCount(String mutationType) {
        return improvements.containsKey(mutationType) ? improvements.get(mutationType) : 0;
    }
}
