package org.floric.studies.evo.project2.solver;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BruteForceSolver implements ISolver {
    @Override
    public Solution solve(Map<Integer, Double[]> positions) {
        Evaluator ev = new Evaluator(positions);

        long startTime = System.currentTimeMillis();
        ImmutableList<Integer> partA = Solution.generatePartOfGenotype(positions.size());
        ImmutableList<Integer> partB = Solution.generatePartOfGenotype(positions.size());
        ImmutableList<Integer> partC = Solution.generatePartOfGenotype(positions.size());

        ImmutableList<Integer> bestGen = ImmutableList.of();
        double bestScore = 0.0;

        Collection<List<Integer>> partAPermutations = Collections2.permutations(partA);
        Collection<List<Integer>> partBPermutations = Collections2.permutations(partB);
        Collection<List<Integer>> partCPermutations = Collections2.permutations(partC);
        long totalCount = partAPermutations.size() * partBPermutations.size() * partCPermutations.size();
        long currentIndex = 0L;

        for (List<Integer> starter : partAPermutations) {
            for (List<Integer> main : partBPermutations) {
                for (List<Integer> desert : partCPermutations) {
                    List<Integer> combined = Lists.newArrayList();
                    combined.addAll(starter);
                    combined.addAll(main);
                    combined.addAll(desert);

                    ImmutableList<Integer> gen = ImmutableList.copyOf(combined);
                    double score = ev.evaluate(Solution.fromGenotype(gen));

                    if (score > bestScore) {
                        bestGen = gen;
                        bestScore = score;
                        System.out.println(String.format("Found %f with iteration %d (%f percent)", bestScore, currentIndex, (double) 100.0 * currentIndex / totalCount));
                    }

                    currentIndex++;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Took %f seconds", (endTime - startTime) / 1000.0));

        return Solution.fromGenotype(bestGen);
    }

    @Override
    public String getName() {
        return "Bruteforce Algorithm";
    }
}
