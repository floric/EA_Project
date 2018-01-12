package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import org.floric.studies.evo.project2.model.Meal;
import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.model.Team;

import java.util.*;

public class Evaluator {
    private Map<Integer, Double[]> positions = new HashMap<>();
    private static final double COOK_INFLUENCE = 10.0;
    private static final double MEALS_INFLUENCE = 10.0;
    private static final double TEAMS_INFLUENCE = 1.0;
    private static final double BASIC_SCORE_EXPONENT = 100.0;

    public Evaluator(Map<Integer, Double[]> positions) {
        this.positions = positions;
    }

    public double evaluate(ImmutableList<Integer> s) {
        return evaluate(Solution.fromGenotype(s));
    }

    public double evaluate(Solution s) {
        double score = 0;

        // forced
        // every team is once a cook
        Set<Team> teams = s.getTeams();
        int teamsCount = teams.size();
        for (Team t : teams) {
            Optional<Meal> cookMeal = t.getCookMeal();
            if (cookMeal.isPresent()) {
                score += COOK_INFLUENCE;
            }
        }

        // every team has one starter, one main and one desert meal
        for (Team t : teams) {
            List<Optional<Meal>> meals = Arrays.asList(t.getStarterMeal(), t.getMainMeal(), t.getDesertMeal());
            for (Optional<Meal> meal : meals) {
                if (meal.isPresent()) {
                    score += MEALS_INFLUENCE;
                }
            }
        }

        // possible
        // every team meets 6 other teams during lunches
        for (Team t : teams) {
            score += t.getMeetTeams().size() * TEAMS_INFLUENCE;
        }

        double normalizedScore = Math.max(0, 1.0 - (Math.abs((score / (teamsCount * (COOK_INFLUENCE + 3 * MEALS_INFLUENCE + 6 * TEAMS_INFLUENCE))) - 1.0)));

        // minimal distance to travel for each team
        double totalDistance = getTotalDistance(teams);
        double normalizedDistance = teamsCount / totalDistance;

        score = Math.pow(normalizedScore, BASIC_SCORE_EXPONENT) * normalizedDistance;

        return score;
    }

    private double getDistanceForRoute(ArrayList<Integer> route) {
        double distance = 0.0;
        for (int i = 0; i < route.size() - 1; i++) {
            double curX = positions.get(route.get(i))[0];
            double curY = positions.get(route.get(i))[1];
            double nextX = positions.get(route.get(i + 1))[0];
            double nextY = positions.get(route.get(i + 1))[1];

            distance += Math.sqrt(Math.pow(nextX - curX, 2) + Math.pow(nextY - curY, 2));
        }

        return distance;
    }

    public double getTotalDistance(Set<Team> teams) {
        double totalDistance = 0.0;
        for (Team t : teams) {
            ArrayList<Integer> route = new ArrayList<>();

            // add start
            route.add(t.getName());

            // add travel steps for each meal or return to home
            List<Optional<Meal>> meals = Arrays.asList(t.getStarterMeal(), t.getMainMeal(), t.getDesertMeal());
            for (Optional<Meal> meal : meals) {
                if (meal.isPresent()) {
                    route.add(meal.get().getCook());
                } else {
                    route.add(t.getName());
                }
            }

            // no return journey included, because sometimes with after party or going home

            totalDistance += getDistanceForRoute(route);
        }

        return totalDistance;
    }
}
