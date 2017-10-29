package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.Random;

public class Mutator {
    public Mutator() {

    }

    public Solution mutate(Solution s) {
        Solution mutated = s.getCopy();

        Random rnd = new Random();
        int mutationsPerIndividuum = rnd.nextInt(3);
        String newGen = s.getGenotype();
        for (int i = 0; i < mutationsPerIndividuum; i++) {
            newGen = swapRandom(newGen);
        }

        mutated = Solution.fromGenotype(newGen);

        return mutated;
    }

    private String swapRandom(String s) {
        Random rnd = new Random();
        int first = rnd.nextInt(s.length() - 1);
        int second = rnd.nextInt(s.length() - 1);

        if (first > second) {
            int tmp = first;
            first = second;
            second = first;
        }

        String firstVal = s.substring(first, first + 1);
        String secondVal = s.substring(second, second + 1);

        String changed = setVal(s, firstVal, second);
        return setVal(changed, secondVal, first);
    }

    private String setVal(String s, String newVal, int pos) {
        if (pos >= s.length()) {
            throw new RuntimeException("Illegal position");
        }

        return s.substring(0, pos) + newVal + s.substring(pos + 1, s.length());
    }
}
