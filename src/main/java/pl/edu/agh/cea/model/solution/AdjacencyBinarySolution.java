package pl.edu.agh.cea.model.solution;

import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;

import java.util.List;

/**
 * AdjacencySolution implementor, it is based on bits genotype
 */
public class AdjacencyBinarySolution extends DefaultBinarySolution implements AdjacencySolution<AdjacencyBinarySolution, BinarySet> {
    private final List<AdjacencyBinarySolution> neighbours;

    public AdjacencyBinarySolution(List<Integer> bitsPerVariable, int numberOfObjectives, List<AdjacencyBinarySolution> neighbours) {
        super(bitsPerVariable, numberOfObjectives);
        this.neighbours = neighbours;
    }

    public AdjacencyBinarySolution(AdjacencyBinarySolution solution) {
        super(solution);
        this.neighbours = solution.neighbours;
    }

    @Override
    public List<AdjacencyBinarySolution> getNeighbours() {
        return neighbours;
    }

    @Override
    public AdjacencyBinarySolution copy() {
        return new AdjacencyBinarySolution(this);
    }
}
