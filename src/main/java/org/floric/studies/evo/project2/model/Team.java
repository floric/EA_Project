package org.floric.studies.evo.project2.model;

import com.google.common.collect.Sets;

import java.util.*;

public class Team {
    private int name;
    private Optional<Meal> starterMeal = Optional.empty();
    private Optional<Meal> mainMeal = Optional.empty();
    private Optional<Meal> desertMeal = Optional.empty();

    public Team(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    public Optional<Meal> getStarterMeal() {
        return starterMeal;
    }

    public void setStarterMeal(Optional<Meal> starterMeal) {
        this.starterMeal = starterMeal;
    }

    public Optional<Meal> getMainMeal() {
        return mainMeal;
    }

    public void setMainMeal(Optional<Meal> mainMeal) {
        this.mainMeal = mainMeal;
    }

    public Optional<Meal> getDesertMeal() {
        return desertMeal;
    }

    public void setDesertMeal(Optional<Meal> desertMeal) {
        this.desertMeal = desertMeal;
    }

    public Optional<Meal> getCookMeal() {
        List<Optional<Meal>> meals = Arrays.asList(getStarterMeal(), getMainMeal(), getDesertMeal());

        for (Optional<Meal> meal : meals) {
            if (meal.isPresent() && meal.get().getCook() == this.name) {
                return meal;

            }
        }

        return Optional.empty();
    }

    public Set<Integer> getMeetTeams() {
        Set<Integer> meetTeams = Sets.newHashSet();
        meetTeams.addAll(getMeetTeamsForMeal(starterMeal));
        meetTeams.addAll(getMeetTeamsForMeal(mainMeal));
        meetTeams.addAll(getMeetTeamsForMeal(desertMeal));
        meetTeams.remove(name);
        return meetTeams;
    }

    private Set<Integer> getMeetTeamsForMeal(Optional<Meal> m) {
        Set<Integer> meetTeams = Sets.newHashSet();
        if (m.isPresent()) {
            meetTeams.addAll(m.get().getGuests());
            meetTeams.add(m.get().getCook());
        }
        return meetTeams;
    }
}
