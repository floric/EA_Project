package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.Random;

public class Mutator {
    public static final double MAX_MUTATIONS_PER_INVIDUUM = 0.3;

    public Mutator() {
    }

    public Solution mutate(Solution s) {
        Random rnd = new Random();
        String newGen = s.getGenotype();
        int maxMutations = (int) (newGen.length() * MAX_MUTATIONS_PER_INVIDUUM);
        int mutationsPerIndividuum = rnd.nextInt(maxMutations > newGen.length() ? newGen.length() : maxMutations);
        for (int i = 0; i < mutationsPerIndividuum; i++) {
            newGen = swapRandom(newGen);
        }

        return Solution.fromGenotype(newGen);
    }

    private String swapRandom(String s) {
        Random rnd = new Random();
        int first = rnd.nextInt(s.length());
        int second = rnd.nextInt(s.length());

        if (first > second) {
            int tmp = first;
            first = second;
            second = tmp;
        }

        Character firstVal = s.charAt(first);
        Character secondVal = s.charAt(second);

        String changed = setVal(s, firstVal, second);
        return setVal(changed, secondVal, first);
    }

    private String setVal(String s, Character newVal, int pos) {
        if (pos >= s.length()) {
            throw new RuntimeException("Illegal position");
        }

        return s.substring(0, pos) + newVal + s.substring(pos + 1, s.length());
    }
}
