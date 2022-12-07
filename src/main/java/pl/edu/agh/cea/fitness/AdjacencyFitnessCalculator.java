package pl.edu.agh.cea.fitness;

import org.uma.jmetal.problem.Problem;
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
     * @param problem - problem which is solving
     */
    void calculate(List<S> population, Problem<S> problem);
}
