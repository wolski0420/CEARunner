package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.Set;

public interface AdjacencyMutationOperator<Source> extends MutationOperator<Source> {
    void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions);
}
