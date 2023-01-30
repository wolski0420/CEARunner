package pl.edu.agh.cea.problems;

import org.uma.jmetal.problem.multiobjective.lz09.LZ09F1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.ArrayList;

public class AdjacencyDoubleLZ09 extends LZ09F1 {
    @Override
    public DoubleSolution createSolution() {
        return new AdjacencyDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints(), new ArrayList<>());
    }
}
