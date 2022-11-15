package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.Set;

public class AdjacencyBitFlipMutation extends BitFlipMutation implements AdjacencyMutationOperator<BinarySolution> {
    private Set<? extends AdjacencySolution<?, ?>> setOfIterAwardedSolutions;

    public AdjacencyBitFlipMutation(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    // @TODO extending mutation operator there
}
