package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.Random;

public class Mutator {
    public Mutator() {
    }

    public Solution mutate(Solution s) {
        Random rnd = new Random();
        String newGen = s.getGenotype();
        int mutationsPerIndividuum = rnd.nextInt(newGen.length());
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
