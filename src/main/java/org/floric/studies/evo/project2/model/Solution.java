package org.floric.studies.evo.project2.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    private String genotype = "";
    private double score = 0.0;

    private Solution() {
    }

    public Solution(int count) {
        this.genotype = generateGenotype(count);
    }

    public static Solution fromGenotype(String genotype) {
        Solution s = new Solution();
        s.setGenotype(genotype);
        s.setScore(0.0);
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
            Team t = new Team(genotype.charAt(i));
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
            if (combinedTeams.contains(t.getName().toString())) {
                return Optional.of(new Meal(combinedTeams.charAt(0), combinedTeams.substring(1).toCharArray()));
            }
        }

        return Optional.empty();
    }

    public int getTeamsCount() {
        return genotype.length() / 3;
    }

    public Solution getCopy() {
        Solution solution = Solution.fromGenotype(this.genotype);
        solution.setScore(this.score);
        return solution;
    }

    public Optional<Team> getTeam(Character name) {
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
