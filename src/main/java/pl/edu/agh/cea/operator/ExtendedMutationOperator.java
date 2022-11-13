package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;

import java.util.Set;

public interface ExtendedMutationOperator<Source> extends MutationOperator<Source> {
    void setIterAwardedSolutions(Set<? extends Solution<?>> newIterAwardedSolutions);
}
