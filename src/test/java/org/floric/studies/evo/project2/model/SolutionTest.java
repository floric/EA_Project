package org.floric.studies.evo.project2.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SolutionTest {
    @Test
    public void fromGenotype() throws Exception {
        ImmutableList<Integer> gen = ImmutableList.of(0,1,2,3,4,5,6,7,8);
        Solution s = Solution.fromGenotype(gen);

        assertEquals(gen, s.getGenotype());
    }

    @Test
    public void getTeams() throws Exception {
        Solution s = Solution.fromGenotype(ImmutableList.of(0,1,2,1,0,2,2,0,1));
        Set<Team> teams = s.getTeams();
        assertEquals(3, teams.size());
        List<Team> list = teams.stream().filter(t -> t.getName() == 0).collect(Collectors.toList());
        assertEquals(1, list.size());
        Team teamOne = list.get(0);

        assertTrue(teamOne.getStarterMeal().isPresent());
        Meal starterMeal = teamOne.getStarterMeal().get();
        assertEquals(0, starterMeal.getCook());
        assertTrue(starterMeal.getGuests().contains(1) && starterMeal.getGuests().contains(2) && !starterMeal.getGuests().contains(0));
        assertTrue(teamOne.getMainMeal().isPresent());
        Meal mainMeal = teamOne.getMainMeal().get();
        assertEquals(1, mainMeal.getCook());
        assertTrue(mainMeal.getGuests().contains(0));
        assertTrue(teamOne.getDesertMeal().isPresent());
        Meal desertMeal = teamOne.getDesertMeal().get();
        assertEquals(2, desertMeal.getCook());
        assertTrue(desertMeal.getGuests().contains(0));
    }

    @Test
    public void getTeamsCount() throws Exception {
        Solution s = new Solution(0);
        assertEquals(0, s.getTeamsCount());

        s = Solution.fromGenotype(ImmutableList.of(1, 1, 1));
        assertEquals(1, s.getTeamsCount());

        s = Solution.fromGenotype(ImmutableList.of(1,2,2,1,1,2));
        assertEquals(2, s.getTeamsCount());
    }

    @Test
    public void getTeamsWithMissingAssignments() throws Exception {
        Solution s = Solution.fromGenotype(ImmutableList.of(0,1,2,2,1,9,1,2,0));

        Team teamOne = s.getTeam(0).get();

        assertFalse(teamOne.getMainMeal().isPresent());
        assertTrue(teamOne.getDesertMeal().isPresent());
        assertTrue(teamOne.getStarterMeal().isPresent());
    }

    @Test
    public void getTeamsWithUnassignedCooks() throws Exception {
        Solution s = Solution.fromGenotype(ImmutableList.of(0,1,2,0,1,2,1,2,0));

        Team teamOne = s.getTeam(0).get();
        Team teamThree = s.getTeam(2).get();

        assertTrue(teamOne.getCookMeal().isPresent());
        assertFalse(teamThree.getCookMeal().isPresent());
    }
}