package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Set;

public class ExtendedPolynomialMutation extends PolynomialMutation implements ExtendedMutationOperator<DoubleSolution> {
    private Set<? extends Solution<?>> setOfIterAwardedSolutions;

    public ExtendedPolynomialMutation(double mutationProbability, double distributionIndex) {
        super(mutationProbability, distributionIndex);
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends Solution<?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    // @TODO extending mutation operator there
}
