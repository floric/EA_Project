package org.floric.studies.evo.project2.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    private ImmutableList<Integer> genotype = ImmutableList.of();
    private double score = 0.0;

    private Solution() {
    }

    public Solution(int count) {
        this.genotype = generateGenotype(count);
    }

    public static Solution fromGenotype(ImmutableList<Integer> genotype) {
        Solution s = new Solution();
        s.setGenotype(genotype);
        s.setScore(0.0);
        return s;
    }

    public static ImmutableList<Integer> generateGenotype(int count) {
        List<Integer> genotype = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            genotype.addAll(generatePartOfGenotype(count));
        }
        return ImmutableList.copyOf(genotype);
    }

    public static ImmutableList<Integer> generateRandomGenotype(int count) {
        List<Integer> genotype = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            genotype.addAll(generateRandomPartOfGenotype(count));
        }
        return ImmutableList.copyOf(genotype);
    }

    private static ImmutableList<Integer> generatePartOfGenotype(int count) {
        return ImmutableList.copyOf(IntStream.range(0, count).boxed().collect(Collectors.toList()));
    }

    private static ImmutableList<Integer> generateRandomPartOfGenotype(int count) {
        ImmutableList<Integer> list = generatePartOfGenotype(count);
        Collections.shuffle(Lists.newArrayList(list));
        return ImmutableList.copyOf(list);
    }

    public ImmutableList<Integer> getGenotype() {
        return this.genotype;
    }

    private void setGenotype(ImmutableList<Integer> gen) {
        this.genotype = gen;
    }

    public Set<Team> getTeams() {
        Set<Team> teams = new HashSet<>();
        for (int i = 0; i < getTeamsCount(); i++) {
            Team t = new Team(genotype.get(i));
            List<Integer> starterPart = genotype.subList(0, getTeamsCount());
            List<Integer> mainPart = genotype.subList(getTeamsCount(), getTeamsCount() * 2);
            List<Integer> desertPart = genotype.subList(getTeamsCount() * 2, getTeamsCount() * 3);

            t.setStarterMeal(findAssignedMeal(t, starterPart));
            t.setMainMeal(findAssignedMeal(t, mainPart));
            t.setDesertMeal(findAssignedMeal(t, desertPart));
            teams.add(t);
        }

        return teams;
    }

    private Optional<Meal> findAssignedMeal(Team t, List<Integer> part) {
        for (int j = 0; j < part.size(); j = j + 3) {
            List<Integer> combinedTeams = part.subList(j, j + 3);
            if (combinedTeams.contains(t.getName())) {
                return Optional.of(new Meal(combinedTeams.get(0), combinedTeams.subList(1, combinedTeams.size())));
            }
        }

        return Optional.empty();
    }

    public int getTeamsCount() {
        return genotype.size() / 3;
    }

    public Solution getCopy() {
        Solution solution = Solution.fromGenotype(this.genotype);
        solution.setScore(this.score);
        return solution;
    }

    public Optional<Team> getTeam(int name) {
        return getTeams().stream().filter(t -> t.getName() == name).findFirst();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Team t : getTeams()) {
            stringBuilder.append(t.getName()).append(": ");
            List<Optional<Meal>> meals = Arrays.asList(t.getStarterMeal(), t.getMainMeal(), t.getDesertMeal());
            String mealsLine = meals.stream()
                    .map(m -> m.isPresent() ? m.get().toString() : "-")
                    .reduce((a, b) -> String.format("%s | %s", a, b))
                    .orElse("");
            stringBuilder.append(mealsLine).append("\n");
        }

        return stringBuilder.toString();
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
