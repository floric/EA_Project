package org.floric.studies.evo.project2.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    private String genotype = "";

    public Solution() {
    }

    public Solution(int count) {
        this.genotype = generateGenotype(count);
    }

    public static Solution fromGenotype(String genotype) {
        Solution s = new Solution();
        s.setGenotype(genotype);
        return s;
    }

    public static String generateGenotype(int count) {
        return IntStream.range(0, 3)
                .mapToObj(i -> generatePartOfGenotype(count))
                .collect(Collectors.joining());
    }

    private static String generatePartOfGenotype(int count) {
        return IntStream.range('0', '0' + count)
                .mapToObj(c -> "" + (char) c)
                .collect(Collectors.joining());
    }

    public String getGenotype() {
        return this.genotype;
    }

    private void setGenotype(String gen) {
        this.genotype = gen;
    }

    public Set<Team> getTeams() {
        Set<Team> teams = new HashSet<>();
        for (int i = 0; i < getTeamsCount(); i++) {
            Team t = new Team(genotype.substring(i, i + 1));
            String starterPart = genotype.substring(0, getTeamsCount());
            String mainPart = genotype.substring(getTeamsCount(), getTeamsCount() * 2);
            String desertPart = genotype.substring(getTeamsCount() * 2, getTeamsCount() * 3);

            t.setStarterMeal(findAssignedMeal(t, starterPart));
            t.setMainMeal(findAssignedMeal(t, mainPart));
            t.setDesertMeal(findAssignedMeal(t, desertPart));
            teams.add(t);
        }

        return teams;
    }

    private Optional<Meal> findAssignedMeal(Team t, String part) {
        for (int j = 0; j < part.length(); j = j + 3) {
            String combinedTeams = part.substring(j, j + 3);
            if (combinedTeams.contains(t.getName())) {
                return Optional.of(new Meal(combinedTeams.substring(0, 1), combinedTeams.substring(1).split("")));
            }
        }

        return Optional.empty();
    }

    public int getTeamsCount() {
        return genotype.length() / 3;
    }

    public Solution getCopy() {
        return Solution.fromGenotype(this.genotype);
    }

    public Optional<Team> getTeam(String name) {
        return getTeams().stream().filter(t -> t.getName().equals(name)).findFirst();
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
                    .get();
            stringBuilder.append(mealsLine).append("\n");
        }

        return stringBuilder.toString();
    }
}
