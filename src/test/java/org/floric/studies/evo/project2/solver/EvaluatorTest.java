package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluatorTest {

    public static final double DELTA = 0.01;

    @Test
    public void evalutateEmptySolution() {
        Solution s = new Solution();
        s.fromGenotype("");

        Evaluator e = new Evaluator();
        double score = e.evaluate(s);
        assertEquals(0, score, DELTA);
    }

    @Test
    public void evaluateSolutionWithThreeTeams() {
        Solution s = new Solution();
        s.fromGenotype("012102201");

        Evaluator e = new Evaluator();
        double score = e.evaluate(s);
        assertEquals(120, score, DELTA);
    }
}