package pl.edu.agh.cea.problems;

import org.uma.jmetal.problem.multiobjective.zdt.ZDT6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.ArrayList;

/**
 * Class extending typical ZDT5 problem by creating Adjacency solutions
 */
public class AdjacencyDoubleZDT6 extends ZDT6 {
    @Override
    public DoubleSolution createSolution() {
        return new AdjacencyDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints(), new ArrayList<>());
    }
}
