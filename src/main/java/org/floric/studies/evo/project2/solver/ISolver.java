package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

import java.util.Map;

public interface ISolver {
    Solution solve(Map<Integer, Double[]> positions);
    String getName();
}
