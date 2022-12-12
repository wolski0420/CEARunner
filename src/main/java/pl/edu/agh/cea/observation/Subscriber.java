package pl.edu.agh.cea.observation;

import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.List;

/**
 * Something that is waiting for ping from Observable and the updates its collected data for future manipulation
 */
public interface Subscriber {
    /**
     * Updates its collected data, method used to ping from Observable
     * @param population - list of solutions to watch
     */
    void update(List<? extends AdjacencySolution<?, ?>> population);
}
