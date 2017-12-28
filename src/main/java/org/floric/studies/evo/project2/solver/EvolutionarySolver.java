package org.floric.studies.evo.project2.solver;

import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EvolutionarySolver {

    public EvolutionarySolver() {
    }

    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);
        int individualsCount = 10;
        int iterations = 1000;
        double bestValue = Double.MIN_VALUE;
        List<Integer> bestIndividuum = Lists.newArrayList();

        // start
        List<List<Integer>> individuals = Lists.newArrayList();
        for (int i = 0; i < individualsCount; i++) {
            List<Integer> initialPermutation = Solution.generateRandomGenotype(positions.size());
            individuals.add(initialPermutation);
        }

        // loop
        for (int i = 0; i < iterations; i++) {
            // mutate
            List<List<Integer>> mutated = Lists.newArrayList();
            for (List<Integer> individuum : individuals) {
                mutated.add(mutate(individuum));
            }

            // evaluate
            List<Double> evaluations = Lists.newArrayList();
            for (int j = 0; j < mutated.size(); j++) {
                List<Integer> individuum = mutated.get(j);
                double score = ev.evaluate(Solution.fromGenotype(individuum));
                evaluations.add(score);

                if (score > bestValue) {
                    bestValue = score;
                    bestIndividuum = individuum;
                }
            }

            // normalize scores for selection
            double min = evaluations.stream().mapToDouble(val -> val).min().orElse(0.0);
            double max = evaluations.stream().mapToDouble(val -> val).max().orElse(1.0);
            double sum = evaluations.stream().mapToDouble(val -> val - min).sum();
            double avg = evaluations.stream().mapToDouble(val -> val).average().orElse(0.0);
            double variance = evaluations.stream().mapToDouble(val -> Math.pow((val - avg), 2.0)).sum() / evaluations.size();

            System.out.println(String.format("Avg: %f, Var: %f, Min: %f, Max: %f|%f", avg, variance, min, max, bestValue));

            for (int j = 0; j < evaluations.size(); j++) {
                Double val = evaluations.get(j);
                double fraction = sum == 0.0 ? 1.0 / evaluations.size() : ((val - min) / sum);
                evaluations.set(j, fraction);
            }
            System.out.println(evaluations.stream().map(val -> val * 100.0).collect(Collectors.toList()));

            // select
            individuals.clear();
            for (int j = 0; j < individualsCount; j++) {
                int selectIndex = select(evaluations);
                individuals.add(mutated.get(selectIndex));
            }
        }

        return null;
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

    private List<Integer> mutate(List<Integer> individuum) {
        Random rnd = new Random();
        double mutationVal = rnd.nextDouble();

        if (mutationVal < 0.8) {
            for (int i = 0; i < rnd.nextInt(9) + 1; i++) {
                individuum = Mutator.cyclicSwap(individuum);
            }
        } else if (mutationVal < 0.9) {
            for (int i = 0; i < rnd.nextInt(2) + 1; i++) {
                individuum = Mutator.changeCook(individuum);
            }
        } else {
            individuum = Mutator.changeGuest(individuum);
        }

        return individuum;
    }
}
