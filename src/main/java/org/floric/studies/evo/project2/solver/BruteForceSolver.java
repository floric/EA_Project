package org.floric.studies.evo.project2.solver;

import com.google.common.collect.Collections2;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceSolver implements ISolver {
    @Override
    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);

        long startTime = System.currentTimeMillis();
        ImmutableList<Integer> part = Solution.generatePartOfGenotype(positions.size());

        Collection<List<Integer>> permutations = Collections2.permutations(part).stream().filter((List<Integer> p) -> {
            for (int i = 0; i < p.size() / 3; i++) {
                int index = i * 3;
                // filter cook permutations
                if ((i + 1) * 3 < p.size()) {
                    int a = p.get(index);
                    int b = p.get((i + 1) * 3);
                    if (b < a) {
                        return false;
                    }
                }

                // filter guest permutations
                int guestA = p.get(index + 1);
                int guestB = p.get(index + 2);
                if (guestA > guestB) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());

        long totalCount = permutations.stream().count() * permutations.stream().count() * permutations.stream().count();
        System.out.println(String.format("%d permutations per part, %d total",  permutations.stream().count(), totalCount));


        Optional<ImmutableList<Integer>> solution = permutations.parallelStream().map(starter -> {
            ImmutableList<Integer> bestGen = ImmutableList.of();
            double bestScore = 0.0;

            for (List<Integer> main : permutations) {
                for (List<Integer> desert : permutations) {
                    List<Integer> combined = Lists.newArrayList();
                    combined.addAll(starter);
                    combined.addAll(main);
                    combined.addAll(desert);

                    ImmutableList<Integer> gen = ImmutableList.copyOf(combined);
                    double score = ev.evaluate(Solution.fromGenotype(gen));

                    if (score > bestScore) {
                        bestGen = gen;
                        bestScore = score;
                    }
                }
            }

            System.out.println(String.format("Found %f: %s", bestScore, bestGen));

            return bestGen;
        }).max(Comparator.comparingDouble(a -> ev.evaluate(Solution.fromGenotype(a))));

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Took %f seconds", (endTime - startTime) / 1000.0));

        return Solution.fromGenotype(solution.get());
    }

    @Override
    public String getName() {
        return "Bruteforce Algorithm";
    }
}
