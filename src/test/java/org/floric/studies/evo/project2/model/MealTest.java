package org.floric.studies.evo.project2.model;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class MealTest {
    @Test
    public void getCook() throws Exception {
        Meal m = new Meal(123);

        assertEquals(123, m.getCook());
    }

    @Test
    public void getGuestsWithStrings() throws Exception {
        int guestA = 1;
        int guestB = 2;
        int cookA = 3;

        Meal m = new Meal(cookA, Lists.newArrayList(guestA, guestB));

        assertEquals(cookA, m.getCook());
        assertTrue(m.getGuests().contains(guestA));
        assertTrue(m.getGuests().contains(guestB));
    }
}