package org.floric.studies.evo.project2.model;

import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;

public class Meal {
    private int cook = 0;
    private List<Integer> guests = Lists.newArrayList();

    public Meal(int cook) {
        this.cook = cook;
    }

    public Meal(int cook, List<Integer> guests) {
        this.cook = cook;
        this.guests = guests;
    }

    public int getCook() {
        return cook;
    }

    public List<Integer> getGuests() {
        return guests;
    }

    @Override
    public String toString() {
        return String.format("%d with (%s)",
                getCook(),
                getGuests().stream()
                        .map(String::valueOf)
                        .reduce((a, b) -> String.format("%s,%s", a, b))
                        .get());
    }
}
