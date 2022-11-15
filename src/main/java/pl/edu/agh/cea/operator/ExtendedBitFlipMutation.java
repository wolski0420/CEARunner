package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

import java.util.Set;

public class ExtendedBitFlipMutation extends BitFlipMutation implements ExtendedMutationOperator<BinarySolution> {
    private Set<? extends Solution<?>> setOfIterAwardedSolutions;

    public ExtendedBitFlipMutation(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends Solution<?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    // @TODO extending mutation operator there
}
