package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.Set;

/**
 * Interface for obligating AdjacencySolutions to be mutated with iterAwardedSolutions
 * @param <Source> - type of Solution to mutate
 */
public interface AdjacencyMutationOperator<Source> extends MutationOperator<Source> {
    void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions);
}
