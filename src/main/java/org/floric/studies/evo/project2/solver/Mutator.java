package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Mutator {
    public Mutator() {
    }

    @Data
    @AllArgsConstructor
    class MutationResult {
        private ImmutableList<Integer> individuum;
        private String mutationType;
    }

    public MutationResult mutate(ImmutableList<Integer> individuum) {
        Map<String, Double> mutationWeights = Maps.newHashMap();
        mutationWeights.put("cyclicSwap", 0.3333);
        mutationWeights.put("changeCook", 0.3333);
        mutationWeights.put("swapGuests", 0.3333);

        return mutate(individuum, mutationWeights);
    }

    public MutationResult mutate(ImmutableList<Integer> individuum, Map<String, Double> mutationWeights) {
        Random rnd = new Random();
        double mutationVal = rnd.nextDouble();

        String mutationType = "";
        double cyclicSwapPro = mutationWeights.get("cyclicSwap");
        double changeCookPro = mutationWeights.get("changeCook");

        if (mutationVal < cyclicSwapPro) {
            for (int i = 0; i < rnd.nextInt(5) + 1; i++) {
                individuum = Mutator.cyclicSwap(individuum);
            }
            mutationType = "cyclicSwap";
        } else if (mutationVal < changeCookPro + cyclicSwapPro) {
            for (int i = 0; i < rnd.nextInt(2) + 1; i++) {
                individuum = Mutator.changeCook(individuum);
            }
            mutationType = "changeCook";
        } else {
            individuum = Mutator.swapGuests(individuum);
            mutationType = "swapGuests";
        }

        return new MutationResult(individuum, mutationType);
    }

    public static ImmutableList<Integer> cyclicSwap(ImmutableList<Integer> s) {
        Random rnd = new Random();
        return cyclicSwap(s, rnd.nextLong());
    }

    public static ImmutableList<Integer> changeCook(ImmutableList<Integer> s) {
        Random rnd = new Random();
        return changeCook(s, rnd.nextLong());
    }

    public static ImmutableList<Integer> swapGuests(ImmutableList<Integer> s) {
        Random rnd = new Random();
        return swapGuests(s, rnd.nextLong());
    }

    public static ImmutableList<Integer> cyclicSwap(ImmutableList<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int cookAIndex = rnd.nextInt(teamsCount) * 3;
        int guestBIndex = cookAIndex + rnd.nextInt(2) + 1;

        int teamA = s.get(cookAIndex);
        int teamB = s.get(guestBIndex);

        int cookBIndex = 0;
        int guestAIndex = 0;

        // find usage of B as cook
        for (int i = 0; i < s.size(); i++) {
            // check only cooks
            if (i % 3 != 0) {
                continue;
            }

            int teamIndex = s.get(i);
            if (teamIndex == teamB) {
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
            if (teamIndex == teamA) {
                guestAIndex = i;
                break;
            }
        }

        Collections.swap(s, cookAIndex, guestBIndex);
        Collections.swap(s, guestAIndex, cookBIndex);

        return ImmutableList.copyOf(s);
    }

    public static ImmutableList<Integer> changeCook(ImmutableList<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int cookIndex = rnd.nextInt(teamsCount) * 3;
        int guestIndex = cookIndex + rnd.nextInt(2) + 1;

        Collections.swap(s, cookIndex, guestIndex);

        return ImmutableList.copyOf(s);
    }

    public static ImmutableList<Integer> swapGuests(ImmutableList<Integer> original, long seed) {
        List<Integer> s = Lists.newArrayList(original);
        Random rnd = new Random(seed);

        int teamsCount = s.size() / 3;
        int constellationAIndex = rnd.nextInt(teamsCount) * 3 + 1 + rnd.nextInt(2);
        int constellationBIndex = rnd.nextInt(teamsCount) * 3 + 1 + rnd.nextInt(2);

        Collections.swap(s, constellationAIndex, constellationBIndex);

        return ImmutableList.copyOf(s);
    }
}
