package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import pl.edu.agh.cea.model.solution.AdjacencyBinarySolution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * One of the most important tactic in AdjacencyMOCell mechanism
 * Extending there defines, what type of variables we want to use
 * Implementing there obligates us to hold iterAwardedSolutions
 * Mentioned set is used to check if solution has neighbour specially awarded
 */
public class AdjacencyBitFlipMutation extends BitFlipMutation implements AdjacencyMutationOperator<BinarySolution> {
    private final Random random = new Random();
    private Set<? extends AdjacencySolution<?, ?>> setOfIterAwardedSolutions;

    public AdjacencyBitFlipMutation(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    /**
     * Extended mutation process, possible paths:
     * 1. Given solution has a neighbour who is specially awarded -> mutate using its genes.
     * 2. Given solution does not have a specially awarded neighbour -> classic mutate solo
     * @param probability - probability for flipping genes
     * @param solution - given individual
     */
    @Override
    public void doMutation(double probability, BinarySolution solution) {
        Set<? extends AdjacencySolution<?, ?>> awardedNeighbours = ((AdjacencyBinarySolution) solution).getNeighbours().stream()
                .filter(setOfIterAwardedSolutions::contains)
                .collect(Collectors.toSet());

        if (!awardedNeighbours.isEmpty()) {
            AdjacencySolution<?, ?> chosenAwardedNeighbour = new ArrayList<>(awardedNeighbours).get(random.nextInt(awardedNeighbours.size()));

            int randomIndex = random.nextInt(Math.min(solution.variables().size(), chosenAwardedNeighbour.variables().size()));
            for(int i = 0; i < solution.variables().size(); ++i) {
                if (i == randomIndex) {
                    // random variable is going to be copied from neighbour
                    solution.variables().set(randomIndex, (BinarySet) chosenAwardedNeighbour.variables().get(randomIndex));
                } else {
                    // rest of variables are flipped
                    for(int j = 0; j < solution.variables().get(i).getBinarySetLength(); ++j) {
                        if (random.nextDouble(1) <= probability) {
                            solution.variables().get(i).flip(j);
                        }
                    }
                }
            }
        } else {
            super.doMutation(probability, solution);
        }
    }
}
