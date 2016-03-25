package ru.ifmo.ctddev.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class Tuple {
    private List<Tuple> subTuple;
    private Object value;

    public Tuple(Tuple... subTuple) {
        this.subTuple = Arrays.asList(subTuple);
    }

    public Tuple(Integer x) {
        subTuple = new ArrayList<>();
        value = x;
    }

    public Tuple(Boolean x) {
        subTuple = new ArrayList<>();
        value = x;
    }

}
