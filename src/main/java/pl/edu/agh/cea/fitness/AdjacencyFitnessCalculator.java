package pl.edu.agh.cea.fitness;

import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.List;

/**
 * Interface for fitness calculators, they may vary in methodology
 * @param <S>
 */
public interface AdjacencyFitnessCalculator<S extends AdjacencySolution<S, ?>> {
    /**
     * Method to calculate fitness for every population member
     * @param population - list of Solutions
     */
    void calculate(List<S> population);
}
