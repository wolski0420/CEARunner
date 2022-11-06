package pl.edu.agh.cea.model.neighbourhood;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class defining two dimensional mesh structure for MOCell algorithm, it is extended by some additional public methods
 * @param <S>
 *     type of Solution
 */
public class Extended2DMesh<S> implements Neighborhood<S> {
    private final List<List<Integer>> mesh;
    private final List<Pair<Integer, Integer>> cornerNeighbourhood;
    private final List<Pair<Integer, Integer>> sideNeighbourhood;

    /**
     * This constructor takes only size and starts loading basic structures
     * @param rows - rows number
     * @param columns - columns number
     */
    public Extended2DMesh(int rows, int columns) {
        this.mesh = new ArrayList<>();
        this.sideNeighbourhood = new ArrayList<>();
        this.cornerNeighbourhood = new ArrayList<>();

        loadNeighbourhood();
        loadMesh(rows, columns);
    }

    /**
     * Loads indices of 1D arraylist to 2D arraylist
     * @param rows - rows number
     * @param columns - columns number
     */
    private void loadMesh(int rows, int columns) {
        IntStream.range(0, rows)
                .forEach(row -> mesh.add(
                        IntStream.range(row * columns, (row + 1) * columns)
                                .boxed()
                                .collect(Collectors.toList())
                        )
                );
    }

    /**
     * Loads unitary vectors to java arraylist
     */
    private void loadNeighbourhood() {
        this.sideNeighbourhood.addAll(Arrays.asList(
                new MutablePair<>(-1, 0), new MutablePair<>(1, 0),
                new MutablePair<>(0, -1), new MutablePair<>(0, 1)
        ));

        this.cornerNeighbourhood.addAll(Arrays.asList(
                new MutablePair<>(-1, -1), new MutablePair<>(1, -1),
                new MutablePair<>(-1, 1), new MutablePair<>(1, 1)
        ));
    }

    /**
     * Takes solution index from 1D arraylist and returns row index of 2D arraylist
     * @param solution - solution index
     * @return row index
     */
    private int getRow(int solution) {
        return solution / mesh.get(0).size();
    }

    /**
     * Takes solution index from 1D arraylist and returns column index of 2D arraylist
     * @param solution - solution index
     * @return column index
     */
    private int getColumn(int solution) {
        return solution % mesh.get(0).size();
    }

    /**
     * Unwanted input data filter
     * @param solutionList - list of solutions
     * @param solutionPosition - index of solution in given list
     * @return neighbours returned from method findNeighbors(...)
     */
    @Override
    public List<S> getNeighbors(List<S> solutionList, int solutionPosition) {
        if (Objects.isNull(solutionList)) {
            throw new JMetalException("The solution list is null") ;
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty") ;
        } else if (solutionPosition < 0) {
            throw new JMetalException("The solution position value is negative: " + solutionPosition) ;
        } else if (solutionList.size() != mesh.size() * mesh.get(0).size()) {
            throw new JMetalException("The solution list size " + solutionList.size() + " is not"
                    + "equal to the grid size: " + mesh.size() + " * " + mesh.get(0).size()) ;
        }
        else if (solutionPosition >= solutionList.size()) {
            throw new JMetalException("The solution position value " + solutionPosition +
                    " is equal or greater than the solution list size "
                    + solutionList.size()) ;
        }

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

        // first level (the closes neighbourhood)
        List<Pair<Integer, Integer>> allNeighbours = Stream
                .concat(sideNeighbourhood.stream(), cornerNeighbourhood.stream())
                .collect(Collectors.toList());
        List<Pair<Integer, Integer>> levelSideNeighbours = new ArrayList<>(sideNeighbourhood);
        List<Pair<Integer, Integer>> levelCornerNeighbours = new ArrayList<>(cornerNeighbourhood);

        // leveling up
        IntStream.range(1, level).forEach(localLevel -> {
            List<Pair<Integer, Integer>> updatedSides = levelSideNeighbours.stream()
                    .map(pair -> new MutablePair<>(
                            Math.abs(pair.getLeft()) == localLevel ?
                                    pair.getLeft() + (pair.getLeft() / Math.abs(pair.getLeft())) : pair.getLeft(),
                            Math.abs(pair.getRight()) == localLevel ?
                                    pair.getRight() + (pair.getRight() / Math.abs(pair.getRight())) : pair.getRight()
                    ))
                    .collect(Collectors.toList());

            levelCornerNeighbours.forEach(corner -> {
                updatedSides.add(new MutablePair<>(
                        corner.getLeft() + (corner.getLeft() / Math.abs(corner.getLeft())), corner.getRight()
                ));
                updatedSides.add(new MutablePair<>(
                        corner.getLeft(), corner.getRight() + (corner.getRight() / Math.abs(corner.getRight()))
                ));
            });

            levelSideNeighbours.clear();
            levelSideNeighbours.addAll(updatedSides);

            List<Pair<Integer, Integer>> updatedCorners = levelCornerNeighbours.stream()
                    .map(pair -> new MutablePair<>(
                            pair.getLeft() + (pair.getLeft() / Math.abs(pair.getLeft())),
                            pair.getRight() + (pair.getRight() / Math.abs(pair.getRight()))
                    ))
                    .collect(Collectors.toList());

            levelCornerNeighbours.clear();
            levelCornerNeighbours.addAll(updatedCorners);

            allNeighbours.addAll(levelSideNeighbours);
            allNeighbours.addAll(levelCornerNeighbours);
        });

        // mapping vectors to 1. neighbours location, 2. validated neighbours location, 3. neighbours solutions
        return allNeighbours.stream()
                .map(pair -> new MutablePair<>(
                        getRow(solutionPosition) + pair.getLeft(),
                        getColumn(solutionPosition) + pair.getRight()))
                .map(pair -> new MutablePair<>(
                        (pair.getLeft() + mesh.size()) % mesh.size(),
                        (pair.getRight() + mesh.get(0).size()) % mesh.get(0).size()))
                .map(pair -> mesh.get(pair.getLeft()).get(pair.getRight()))
                .map(solutionList::get)
                .collect(Collectors.toList());
    }

    /**
     * Takes solution lists and requested solution coordinates, then returns solution from this location
     * @param solutionList - list of solutions
     * @param row - row number
     * @param column - column number
     * @return - solution located on given coordinates
     */
    public S getSolutionBy2DLocation(List<S> solutionList, int row, int column) {
        return solutionList.get(row * mesh.get(0).size() + column);
    }
}
