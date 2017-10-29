package org.floric.studies.evo.project2.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Meal {
    private String cook = "";
    private Set<String> guests = new HashSet<>();

    public Meal(String cook, String... guests) {
        this.cook = cook;
        this.guests = Stream.of(guests).collect(Collectors.toSet());
    }

    public String getCook() {
        return cook;
    }

    public Set<String> getGuests() {
        return guests;
    }

    @Override
    public String toString() {
        return getCook() + " with (" + getGuests().stream().reduce((a, b) -> String.format("%s,%s", a, b)).get() + ")";
    }
}
