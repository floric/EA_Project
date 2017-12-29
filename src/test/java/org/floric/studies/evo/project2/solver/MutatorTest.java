package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MutatorTest {

    private Mutator mutator;

    @Before
    public void setUp() {
        this.mutator = new Mutator();
    }

    @Test
    public void cyclicSwap() throws Exception {
        ImmutableList<Integer> oldTeams = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9,
                2, 4, 7, 5, 1, 9, 8, 3, 6,
                3, 5, 7, 6, 1, 8, 9, 2, 4
                );
        ImmutableList<Integer> newTeams = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9,
                2, 4, 7, 3, 1, 9, 8, 5, 6,
                5, 3, 7, 6, 1, 8, 9, 2, 4);

        ImmutableList<Integer> swappedTeams = Mutator.cyclicSwap(oldTeams, 1L);
        assertEquals(newTeams, swappedTeams);
    }

    @Test
    public void cyclicSwap2() throws Exception {
        ImmutableList<Integer> oldTeams = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9,
                2, 4, 7, 5, 1, 9, 8, 3, 6,
                3, 5, 7, 6, 1, 8, 9, 2, 4
        );
        ImmutableList<Integer> newTeams = ImmutableList.of(
                5, 2, 3, 4, 1, 6, 7, 8, 9,
                2, 4, 7, 1, 5, 9, 8, 3, 6,
                3, 5, 7, 6, 1, 8, 9, 2, 4);

        ImmutableList<Integer> swappedTeams = Mutator.cyclicSwap(oldTeams, 2L);
        assertEquals(newTeams, swappedTeams);
    }

    @Test
    public void cyclicSwap3() throws Exception {
        ImmutableList<Integer> oldTeams = ImmutableList.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9,
                2, 4, 7, 5, 1, 9, 8, 3, 6,
                3, 5, 7, 6, 1, 8, 9, 2, 4
        );
        ImmutableList<Integer> newTeams = ImmutableList.of(
                1, 2, 3, 9, 5, 6, 7, 8, 4,
                2, 4, 7, 5, 1, 9, 8, 3, 6,
                3, 5, 7, 6, 1, 8, 4, 2, 9);

        ImmutableList<Integer> swappedTeams = Mutator.cyclicSwap(oldTeams, 4L);
        assertEquals(newTeams, swappedTeams);
    }
}