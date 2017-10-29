package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.Random;

public class Mutator {
    public static final int MAX_MUTATIONS_PER_INVIDUUM = 10;

    public Mutator() {

    }

    public Solution mutate(Solution s) {
        Random rnd = new Random();
        int mutationsPerIndividuum = rnd.nextInt(MAX_MUTATIONS_PER_INVIDUUM);
        String newGen = s.getGenotype();
        for (int i = 0; i < mutationsPerIndividuum; i++) {
            newGen = swapRandom(newGen);
        }

        return Solution.fromGenotype(newGen);
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
