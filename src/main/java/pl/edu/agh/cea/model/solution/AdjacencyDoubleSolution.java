package pl.edu.agh.cea.model.solution;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.util.List;

/**
 * AdjacencySolution implementor, it is based on bits genotype
 */
public class AdjacencyDoubleSolution extends DefaultDoubleSolution implements AdjacencySolution<AdjacencyDoubleSolution, Double> {
    private final List<AdjacencyDoubleSolution> neighbours;

    public AdjacencyDoubleSolution(List<Pair<Double, Double>> bounds, int numberOfObjectives, int numberOfConstraints, List<AdjacencyDoubleSolution> neighbours) {
        super(bounds, numberOfObjectives, numberOfConstraints);
        this.neighbours = neighbours;
    }

    public AdjacencyDoubleSolution(AdjacencyDoubleSolution solution) {
        super(solution);
        this.neighbours = solution.neighbours;
    }

    @Override
    public List<AdjacencyDoubleSolution> getNeighbours() {
        return neighbours;
    }

    @Override
    public AdjacencyDoubleSolution copy() {
        return new AdjacencyDoubleSolution(this);
    }
}
