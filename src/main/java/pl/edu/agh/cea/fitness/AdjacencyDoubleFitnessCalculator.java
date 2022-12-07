package pl.edu.agh.cea.fitness;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyDoubleFitnessCalculator implements AdjacencyFitnessCalculator<AdjacencyDoubleSolution> {

    protected List<List<Double>> indicatorValues;
    protected double maxIndicatorValue;

    double calculateHypervolumeIndicator(
            AdjacencyDoubleSolution solutionA,
            AdjacencyDoubleSolution solutionB,
            int d,
            List<Double> maximumValues,
            List<Double> minimumValues
    ) {
        double rho = 2.0;
        double r = rho * (maximumValues.get(d - 1) - minimumValues.get(d - 1));
        double max = minimumValues.get(d - 1) + r;
        double a = solutionA.getObjective(d - 1);
        double b;
        if (solutionB == null) {
            b = max;
        } else {
            b = solutionB.getObjective(d - 1);
        }

        double volume;
        if (d == 1) {
            if (a < b) {
                volume = (b - a) / r;
            } else {
                volume = 0.0;
            }
        } else if (a < b) {
            volume = this.calculateHypervolumeIndicator(solutionA, null, d - 1, maximumValues, minimumValues) * (b - a) / r;
            volume += this.calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues) * (max - b) / r;
        } else {
            volume = this.calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues) * (max - a) / r;
        }

        return volume;
    }

    public void computeIndicatorValuesHD(
            List<AdjacencyDoubleSolution> population,
            Problem<AdjacencyDoubleSolution> problem,
            List<Double> maximumValues,
            List<Double> minimumValues
    ) {
        this.indicatorValues = new ArrayList<>();
        this.maxIndicatorValue = -1.7976931348623157E308;

        for(int i = 0; i < population.size(); i++) {
            List<AdjacencyDoubleSolution> A = new ArrayList<>(1);
            A.add(population.get(i));
            List<Double> aux = new ArrayList<>();

            double value;
            for (AdjacencyDoubleSolution solution : population) {
                List<AdjacencyDoubleSolution> B = new ArrayList<>(1);
                B.add(solution);
                int flag = (new DominanceComparator<AdjacencyDoubleSolution>()).compare(A.get(0), B.get(0));
                if (flag < 0) {
                    value = -this.calculateHypervolumeIndicator(A.get(0), B.get(0), problem.getNumberOfObjectives(), maximumValues, minimumValues);
                } else {
                    value = this.calculateHypervolumeIndicator(B.get(0), A.get(0), problem.getNumberOfObjectives(), maximumValues, minimumValues);
                }

                if (Math.abs(value) > this.maxIndicatorValue) {
                    this.maxIndicatorValue = Math.abs(value);
                }
            }

            this.indicatorValues.add(aux);
        }

    }

    public double fitness(List<AdjacencyDoubleSolution> population, int position) {
        double fitness = 0.0;
        double kappa = 0.05;

        for(int i = 0; i < population.size(); ++i) {
            if (i != position) {
                fitness += Math.exp(-1.0 * (this.indicatorValues.get(i)).get(position) / this.maxIndicatorValue / kappa);
            }
        }

        return fitness;
    }

    @Override
    public void calculate(List<AdjacencyDoubleSolution> population, Problem<AdjacencyDoubleSolution> problem) {
        List<Double> maximumValues = new ArrayList<>(problem.getNumberOfObjectives());
        List<Double> minimumValues = new ArrayList<>(problem.getNumberOfObjectives());

        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            maximumValues.set(i, -1.7976931348623157E308);
            maximumValues.set(i, Double.MAX_VALUE);
        }

        population.forEach(solution -> {
            for(int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
                double value = solution.getObjective(obj);
                maximumValues.set(obj, Math.max(value, maximumValues.get(obj)));
                minimumValues.set(obj, Math.min(value, maximumValues.get(obj)));
            }
            solution.setAttribute(Fitness.class, 0.0);
        });

        computeIndicatorValuesHD(population, problem, maximumValues, minimumValues);

        for(int position = 0; position < population.size(); position++) {
            population.get(position).setAttribute(Fitness.class, fitness(population, position));
        }
    }


}
