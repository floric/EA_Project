package org.floric.studies.evo.project2.model;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SolutionTest {
    @Test
    public void fromGenotype() throws Exception {
        Solution s = new Solution();
        String gen = "012345678";
        s.fromGenotype(gen);

        assertEquals(gen, s.getGenotype());
    }

    @Test
    public void getTeams() throws Exception {
        Solution s = new Solution();
        s.fromGenotype("012102201");
        Set<Team> teams = s.getTeams();
        assertEquals(3, teams.size());
        List<Team> list = teams.stream().filter(t -> t.getName().equals("0")).collect(Collectors.toList());
        assertEquals(1, list.size());
        Team teamOne = list.get(0);

        assertTrue(teamOne.getStarterMeal().isPresent());
        Meal starterMeal = teamOne.getStarterMeal().get();
        assertEquals("0", starterMeal.getCook());
        assertTrue(starterMeal.getGuests().contains("1") && starterMeal.getGuests().contains("2") && !starterMeal.getGuests().contains("0"));
        assertTrue(teamOne.getMainMeal().isPresent());
        Meal mainMeal = teamOne.getMainMeal().get();
        assertEquals("1", mainMeal.getCook());
        assertTrue(mainMeal.getGuests().contains("0"));
        assertTrue(teamOne.getDesertMeal().isPresent());
        Meal desertMeal = teamOne.getDesertMeal().get();
        assertEquals("2", desertMeal.getCook());
        assertTrue(desertMeal.getGuests().contains("0"));
    }

    @Test
    public void getTeamsCount() throws Exception {
        Solution s = new Solution();
        assertEquals(0, s.getTeamsCount());

        s = new Solution();
        s.fromGenotype("111");
        assertEquals(1, s.getTeamsCount());

        s = new Solution();
        s.fromGenotype("122112");
        assertEquals(2, s.getTeamsCount());
    }

    @Test
    public void getTeamsWithMissingAssignments() throws Exception {
        Solution s = new Solution();
        s.fromGenotype("012219120");

        Team teamOne = s.getTeam("0").get();

        assertFalse(teamOne.getMainMeal().isPresent());
        assertTrue(teamOne.getDesertMeal().isPresent());
        assertTrue(teamOne.getStarterMeal().isPresent());
    }

    @Test
    public void getTeamsWithUnassignedCooks() throws Exception {
        Solution s = new Solution();
        s.fromGenotype("012012120");

        Team teamOne = s.getTeam("0").get();
        Team teamThree = s.getTeam("2").get();

        assertTrue(teamOne.getCookMeal().isPresent());
        assertFalse(teamThree.getCookMeal().isPresent());
    }

    @Test
    public void getCopy() throws Exception {
        Solution s = new Solution();
        s.fromGenotype("012120210");
        Set<Team> teams = s.getTeams();

        Solution copy = s.getCopy();
        assertNotEquals(teams, copy.getTeams());
        assertEquals(s.getGenotype(), copy.getGenotype());
        assertEquals(s.getTeamsCount(), copy.getTeamsCount());
    }

}