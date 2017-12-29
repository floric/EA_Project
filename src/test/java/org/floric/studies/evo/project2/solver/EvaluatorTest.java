package org.floric.studies.evo.project2.solver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.floric.studies.evo.project2.main.App;
import org.floric.studies.evo.project2.model.Solution;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluatorTest {

    public static final double DELTA = 0.01;

    @Test
    public void evalutateEmptySolution() {
        Solution s = Solution.fromGenotype(ImmutableList.of());

        Evaluator e = new Evaluator(App.generatePositions(0, 0L));
        double score = e.evaluate(s);
        assertEquals(0, score, DELTA);
    }

    @Test
    public void evaluateSolutionWithThreeTeams() {
        Solution s = Solution.fromGenotype(ImmutableList.of(0,1,2,1,0,2,2,0,1));

        Evaluator e = new Evaluator(App.generatePositions(0, 0L));
        double score = e.evaluate(s);
        assertEquals(129, score, DELTA);
    }
}