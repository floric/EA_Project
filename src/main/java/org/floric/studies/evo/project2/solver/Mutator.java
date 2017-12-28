package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.model.Solution;

import java.util.Collections;
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

    public static List<Integer> cyclicSwap(List<Integer> s) {
        Random rnd = new Random();
        return cyclicSwap(s, rnd.nextLong());
    }

    public static List<Integer> changeCook(List<Integer> s) {
        Random rnd = new Random();
        return changeCook(s, rnd.nextLong());
    }

    public static List<Integer> changeGuest(List<Integer> s) {
        Random rnd = new Random();
        return changeGuest(s, rnd.nextLong());
    }

    public static List<Integer> cyclicSwap(List<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int cookAIndex = rnd.nextInt(teamsCount) * 3;
        int guestBIndex = cookAIndex + rnd.nextInt(2) + 1;

        int cookA = s.get(cookAIndex);
        int cookB = s.get(guestBIndex);

        int cookBIndex = 0;
        int guestAIndex = 0;

        // find usage of B as cook
        for (int i = 0; i < s.size(); i++) {
            // check only cooks
            if (i % 3 != 0) {
                continue;
            }

            int teamIndex = s.get(i);
            if (teamIndex == cookB) {
                cookBIndex = i;
                break;
            }
        }

        int mealIndex = cookBIndex / teamsCount;

        // find usage of A as guest
        for (int i = mealIndex * teamsCount; i < (mealIndex + 1) * teamsCount; i++) {
            // check only guests
            if (i % 3 == 0) {
                continue;
            }

            int teamIndex = s.get(i);
            if (teamIndex == cookA) {
                guestAIndex = i;
                break;
            }
        }

        Collections.swap(s, cookAIndex, guestBIndex);
        Collections.swap(s, guestAIndex, cookBIndex);

        return ImmutableList.copyOf(s);
    }

    public static List<Integer> changeCook(List<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int cookIndex = rnd.nextInt(teamsCount) * 3;
        int guestIndex = cookIndex + rnd.nextInt(2) + 1;

        Collections.swap(s, cookIndex, guestIndex);

        return ImmutableList.copyOf(s);
    }

    public static List<Integer> changeGuest(List<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int constellationAIndex = rnd.nextInt(teamsCount) * 3 + 1 + rnd.nextInt(2);
        int constellationBIndex = rnd.nextInt(teamsCount) * 3 + 1 + rnd.nextInt(2);

        Collections.swap(s, constellationAIndex, constellationBIndex);

        return ImmutableList.copyOf(s);
    }
}
