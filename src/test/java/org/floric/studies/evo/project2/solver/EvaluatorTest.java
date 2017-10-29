package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class EvaluatorTest {

    public static final double DELTA = 0.01;

    @Test
    public void evalutateEmptySolution() {
        Solution s = Solution.fromGenotype("");

        Evaluator e = new Evaluator();
        double score = e.evaluate(s);
        assertEquals(0, score, DELTA);
    }

    @Test
    public void evaluateSolutionWithThreeTeams() {
        Solution s = Solution.fromGenotype("012102201");

        Evaluator e = new Evaluator();
        double score = e.evaluate(s);
        assertEquals(129, score, DELTA);
    }
}