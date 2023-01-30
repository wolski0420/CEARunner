package pl.edu.agh.cea.problems;

import org.uma.jmetal.problem.multiobjective.Schaffer;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.ArrayList;

public class AdjacencyDoubleSchaffer extends Schaffer {
    @Override
    public DoubleSolution createSolution() {
        return new AdjacencyDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints(), new ArrayList<>());
    }
}
