package org.floric.studies.evo.project2.model;

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
        String cook = "CookA";
        Meal m = new Meal(cook);

        assertEquals(cook, m.getCook());
    }

    @Test
    public void getGuestsWithStrings() throws Exception {
        String guestA = "guestA";
        String guestB = "guestB";
        String cookA = "cookA";

        Meal m = new Meal(cookA, guestA, guestB);

        assertEquals(cookA, m.getCook());
        assertTrue(m.getGuests().contains(guestA));
        assertTrue(m.getGuests().contains(guestB));
    }

    @Test
    public void getGuestsWithArray() throws Exception {
        String guestA = "A";
        String guestB = "B";
        String cookA = "cookA";

        Meal m = new Meal(cookA, (guestA + guestB).split(""));

        assertEquals(cookA, m.getCook());
        assertTrue(m.getGuests().contains(guestA));
        assertTrue(m.getGuests().contains(guestB));
    }
}