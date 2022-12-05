package pl.edu.agh.cea.fitness;

import org.uma.jmetal.util.solutionattribute.impl.Fitness;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.List;

public class AdjacencyDoubleFitnessCalculator implements AdjacencyFitnessCalculator<AdjacencyDoubleSolution> {
    @Override
    public void calculate(List<AdjacencyDoubleSolution> population) {
        population.forEach(solution -> solution.setAttribute(Fitness.class, 0.0));

        // @TODO rewrite it
    }
}
