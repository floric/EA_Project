package org.floric.studies.evo.project2.solver;

import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;

import java.util.List;
import java.util.Random;

public class Mutator {
    public Mutator() {
    }

    public Solution mutate(Solution s) {
        Random rnd = new Random();
        List<Integer> newGen = s.getGenotype();
        int mutationsPerIndividuum = (int) (rnd.nextInt(newGen.size()) * 0.5);
        for (int i = 0; i < mutationsPerIndividuum; i++) {
            newGen = swapRandom(newGen);
        }

        return Solution.fromGenotype(newGen);
    }

    private List<Integer> swapRandom(List<Integer> s) {
        Random rnd = new Random();
        int first = rnd.nextInt(s.size());
        int second = rnd.nextInt(s.size());

        if (first > second) {
            int tmp = first;
            first = second;
            second = tmp;
        }

        int firstVal = s.get(first);
        int secondVal = s.get(second);

        List<Integer> changed = setVal(s, firstVal, second);
        return setVal(changed, secondVal, first);
    }

    private List<Integer> setVal(List<Integer> s, int newVal, int pos) {
        if (pos >= s.size()) {
            throw new RuntimeException("Illegal position");
        }

        List<Integer> strings = Lists.newArrayList(s).subList(0, pos);
        strings.add(newVal);
        strings.addAll(s.subList(pos + 1, s.size()));
        return strings;
    }
}
