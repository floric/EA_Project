package org.floric.studies.evo.project2.solver;

import org.floric.studies.evo.project2.model.Solution;

public class HillClimber {

    public HillClimber() {

    }

    public Solution climb(int minPasses, Solution start, Mutator mutator, Evaluator evaluator) {
        Solution bestSolution = start;
        double bestScore = evaluator.evaluate(bestSolution);
        int lastClimb = 0;
        int i = 0;

        while(i < lastClimb * 3 || i < minPasses) {
            Solution newSolution = mutator.mutate(bestSolution);

            double newScore = evaluator.evaluate(newSolution);
            if (newScore > bestScore) {
                bestScore = newScore;
                bestSolution = newSolution;
                lastClimb = i;
                System.out.println(String.format("%d: %f, (%f), distance: %f", i, bestScore, bestScore * 100 / evaluator.getMaxScore(bestSolution), evaluator.getTotalDistance(bestSolution.getTeams())));
                System.out.println(bestSolution.getGenotype());
            }

            i++;
        }

        System.out.println(String.format("Stopped after %d iterations.", i));

        return bestSolution;
    }
}
