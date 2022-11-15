package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import pl.edu.agh.cea.model.solution.AdjacencyBinarySolution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class AdjacencyBitFlipMutation extends BitFlipMutation implements AdjacencyMutationOperator<BinarySolution> {
    private Set<? extends AdjacencySolution<?, ?>> setOfIterAwardedSolutions;

    public AdjacencyBitFlipMutation(double mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    @Override
    public void doMutation(double probability, BinarySolution solution) {
        Set<? extends AdjacencySolution<?, ?>> awardedNeighbours = ((AdjacencyBinarySolution) solution).getNeighbours().stream()
                .filter(setOfIterAwardedSolutions::contains)
                .collect(Collectors.toSet());

        if (!awardedNeighbours.isEmpty()) {
            AdjacencySolution<?, ?> chosenAwardedNeighbour = new ArrayList<>(awardedNeighbours).get(new Random().nextInt(awardedNeighbours.size()));

            // @TODO mutation with neighbour attendance - must be defined how it should look like
        } else {
            super.doMutation(probability, solution);
        }
    }
}
