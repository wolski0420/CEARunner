package pl.edu.agh.cea.observation;

import org.uma.jmetal.util.solutionattribute.impl.Fitness;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mainly it takes fitness attribute value into consideration
 */
public class TypicalFitnessObserver implements Subscriber{
    private final List<List<Double>> fitnessHistory = new ArrayList<>();

    @Override
    public void update(List<? extends AdjacencySolution<?, ?>> population) {
        fitnessHistory.add(population.stream()
                .map(solution -> solution.getAttribute(Fitness.class))
                .map(solution -> (Double) solution)
                .collect(Collectors.toList()));
    }

    /**
     * Converting all collected epochs information to list of average fitness per epoch
     * @return - list with averages per epoch
     */
    public List<Double> getAveragesPerEpoch() {
        return fitnessHistory.stream()
                .map(epoch -> epoch.stream()
                        .mapToDouble(fitnessValue -> fitnessValue)
                        .average()
                        .orElse(0))
                .collect(Collectors.toList());
    }

    // @TODO more data manipulation there
}
