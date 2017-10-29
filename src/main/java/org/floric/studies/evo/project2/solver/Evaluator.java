package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Meal;
import org.floric.studies.evo.project2.model.Solution;
import org.floric.studies.evo.project2.model.Team;

import java.util.*;

public class Evaluator {
    private Map<String, Double[]> positions = new HashMap<>();

    public Evaluator(Map<String, Double[]> positions) {
        this.positions = positions;
    }

    public Evaluator() {
    }

    public double evaluate(Solution s) {
        double score = 0;

        // forced
        // every team is once a cook
        Set<Team> teams = s.getTeams();
        for (Team t : teams) {
            Optional<Meal> cookMeal = t.getCookMeal();
            if (cookMeal.isPresent()) {
                score += 10;
            }
        }

        // every team has one starter, one main and one desert meal
        for (Team t : teams) {
            List<Optional<Meal>> meals = Arrays.asList(t.getStarterMeal(), t.getMainMeal(), t.getDesertMeal());
            for (Optional<Meal> meal : meals) {
                if (meal.isPresent()) {
                    score += 10;
                }
            }
        }

        // possible
        // every team meets 7 other teams during lunches
        for (Team t : teams) {
            Set<String> meetTeams = new HashSet<>();
            List<Optional<Meal>> meals = Arrays.asList(t.getStarterMeal(), t.getMainMeal(), t.getDesertMeal());
            for (Optional<Meal> meal : meals) {
                if (meal.isPresent()) {
                    meetTeams.addAll(meal.get().getGuests());
                    meetTeams.add(meal.get().getCook());
                }
            }

            score += meetTeams.size();
        }

        // minimal distance to travel for each team
        if (positions.size() > 0) {
            double totalDistance = getTotalDistance(teams);
            score = score + 1 / totalDistance;
        }

        return score;
    }

    private double getDistanceForRoute(ArrayList<String> route) {
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

    public double getMaxScore(Solution s) {
        return 47 * s.getTeamsCount();
    }

    public double getTotalDistance(Set<Team> teams) {
        double totalDistance = 0.0;
        for (Team t : teams) {
            ArrayList<String> route = new ArrayList<>();

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

            totalDistance += getDistanceForRoute(route);
        }

        return totalDistance;
    }
}
