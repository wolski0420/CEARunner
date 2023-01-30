package pl.edu.agh.cea.observation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Something that is waiting for ping from Observable and the updates its collected data for future manipulation
 */
public interface Subscriber {
    /**
     * Updates its collected data, method used to ping from Observable
     * @param population - list of solutions to watch
     */
    void update(List<? extends Solution<?>> population);
}
