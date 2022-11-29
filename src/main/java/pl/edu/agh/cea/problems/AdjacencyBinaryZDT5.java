package pl.edu.agh.cea.problems;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import pl.edu.agh.cea.model.solution.AdjacencyBinarySolution;

import java.util.ArrayList;

/**
 * Class extending typical ZDT5 problem by creating Adjacency solutions
 */
public class AdjacencyBinaryZDT5 extends ZDT5 {
    @Override
    public BinarySolution createSolution() {
        return new AdjacencyBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives(), new ArrayList<>());
    }
}
