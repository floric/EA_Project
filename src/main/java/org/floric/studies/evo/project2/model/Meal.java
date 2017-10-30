package org.floric.studies.evo.project2.model;

import com.google.common.primitives.Chars;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Meal {
    private Character cook = '0';
    private Set<Character> guests = new HashSet<>();

    public Meal(char cook, char... guests) {
        this.cook = cook;
        this.guests = new HashSet<>(Chars.asList(guests));
    }

    public Character getCook() {
        return cook;
    }

    public Set<Character> getGuests() {
        return guests;
    }

    @Override
    public String toString() {
        return String.format("%c with (%s)",
                getCook(),
                getGuests().stream()
                        .map(Object::toString)
                        .reduce((a, b) -> String.format("%s,%s", a, b))
                        .get());
    }
}
