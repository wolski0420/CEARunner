package pl.edu.agh.cea.observation;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mainly it takes hyperVolume attribute value into consideration
 */
public class TypicalHyperVolumeObserver implements Subscriber {
    private final List<List<Double>> hyperVolumeHistory = new ArrayList<>();

    @Override
    public void update(List<? extends Solution<?>> population) {
        hyperVolumeHistory.add(population.stream()
                .map(solution -> solution.attributes().get(HypervolumeContributionAttribute.class))
                .map(solution -> (Double) solution)
                .collect(Collectors.toList()));
    }

    /**
     * Converting all collected epochs information to list of average hyperVolume per epoch
     * @return - list with averages per epoch
     */
    public List<Double> getAveragesPerEpoch() {
        return hyperVolumeHistory.stream()
                .map(epoch -> epoch.stream()
                        .mapToDouble(hyperVolumeValue -> hyperVolumeValue)
                        .average()
                        .orElse(0))
                .collect(Collectors.toList());
    }

    // @TODO more data manipulation there
}
