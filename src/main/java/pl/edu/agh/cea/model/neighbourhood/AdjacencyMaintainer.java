package pl.edu.agh.cea.model.neighbourhood;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import pl.edu.agh.cea.model.solution.AdjacencySolution;
import pl.edu.agh.cea.utils.SolutionListValidator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class maintaining neighbourhood area for MOCell algorithm, basing on AdjacencySolution structure
 * @param <S>
 *     type of AdjacencySolution
 */
public class AdjacencyMaintainer<S extends AdjacencySolution<S, ?>> implements Neighborhood<S> {
    private final int rows, columns;

    /**
     * This constructor takes only size
     * @param rows - rows number
     * @param columns - columns number
     */
    public AdjacencyMaintainer(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Unwanted input data filter
     * @param solutionList - list of solutions
     * @param solutionPosition - index of solution in given list
     * @return neighbours returned from method findNeighbors(...)
     */
    @Override
    public List<S> getNeighbors(List<S> solutionList, int solutionPosition) {
        SolutionListValidator.checkSolutionListViolations(solutionList, solutionPosition, rows * columns);
        return getHigherLevelNeighbours(solutionList, solutionPosition, 1);
    }

    /**
     * Takes solutions, given solution index from 1D arraylist and level where to stop finding neighbours (including).
     * Leveling from solution location to level parameter distance. Returning all found neighbours.
     * @param solutionList - list of solutions
     * @param solutionPosition - index of solution in given list
     * @param level - maximum distance from solution where to find and return neighbours
     * @return all neighbours located farthest at level param distance
     */
    public List<S> getHigherLevelNeighbours(List<S> solutionList, int solutionPosition, int level) {
        if (level < 1) {
            throw new JMetalException("Cannot take neighbours of solution from maximum " + level + " level!");
        }

        S actualSolution = solutionList.get(solutionPosition);
        Set<S> levelNeighbours = new HashSet<>(actualSolution.getNeighbours());
        Set<S> allNeighbours = new HashSet<>(levelNeighbours);

        IntStream.range(1, level).forEach(localLevel -> {
            Set<S> foundNewNeighbours = levelNeighbours.stream()
                    .flatMap(solution -> solution.getNeighbours().stream())
                    .filter(neighbour -> !allNeighbours.contains(neighbour))
                    .filter(neighbour -> !actualSolution.equals(neighbour))
                    .collect(Collectors.toSet());

            levelNeighbours.clear();
            levelNeighbours.addAll(foundNewNeighbours);

            allNeighbours.addAll(foundNewNeighbours);
        });

        return allNeighbours.stream().toList();
    }

    /**
     * It replaces old solution specified by index with new provided solution and switched all neighbours
     * @param solutionList - list of solutions
     * @param solutionPosition - index of old solution in given list to replace
     * @param newSolution - new solution to put to
     */
    public void replaceWithNew(List<S> solutionList, int solutionPosition, S newSolution) {
        S actualSolution = solutionList.get(solutionPosition);

        actualSolution.getNeighbours().forEach(neighbour -> {
            neighbour.getNeighbours().remove(actualSolution);
            neighbour.getNeighbours().add(newSolution);
        });
        newSolution.getNeighbours().addAll(actualSolution.getNeighbours());

        solutionList.set(solutionPosition, newSolution);
    }

    /**
     * Takes solution lists and requested solution coordinates, then returns solution from this location
     * @param solutionList - list of solutions
     * @param row - row number
     * @param column - column number
     * @return - solution located on given coordinates
     */
    public S getSolutionBy2DLocation(List<S> solutionList, int row, int column) {
        return solutionList.get(row * rows + column);
    }
}
