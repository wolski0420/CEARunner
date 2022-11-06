package pl.edu.agh.cea.model.solution;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Interface for Solutions visible to other Solutions - neighbourhood at the higher level
 * @param <S>
 *     type of Neighbours
 * @param <T>
 *     type of Variable in Solution
 */
public interface AdjacencySolution<S extends AdjacencySolution<S, T>, T> extends Solution<T> {
    /**
     * Assuming all implementors have List of neighbours (Solutions),
     * it is going to be a typical getter for that List
     * @return List of neighbours as Solutions
     */
    List<S> getNeighbours();
}
