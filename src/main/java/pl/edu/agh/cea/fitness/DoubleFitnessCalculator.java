package pl.edu.agh.cea.fitness;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class DoubleFitnessCalculator implements FitnessCalculator<DoubleSolution> {

    protected List<List<Double>> indicatorValues;
    protected double maxIndicatorValue;

    double calculateHypervolumeIndicator(
            DoubleSolution solutionA,
            DoubleSolution solutionB,
            int d,
            List<Double> maximumValues,
            List<Double> minimumValues
    ) {
        double rho = 2.0;
        double r = rho * (maximumValues.get(d - 1) - minimumValues.get(d - 1));
        double max = minimumValues.get(d - 1) + r;
        double a = solutionA.objectives()[d-1];
        double b;
        if (solutionB == null) {
            b = max;
        } else {
            b = solutionB.objectives()[d-1];
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
            List<DoubleSolution> population,
            Problem<DoubleSolution> problem,
            List<Double> maximumValues,
            List<Double> minimumValues
    ) {
        this.indicatorValues = new ArrayList<>();
        this.maxIndicatorValue = -Double.MAX_VALUE;

        for(int i = 0; i < population.size(); i++) {
            List<DoubleSolution> A = new ArrayList<>(1);
            A.add(population.get(i));
            List<Double> aux = new ArrayList<>();

            double value;
            for (DoubleSolution solution : population) {
                List<DoubleSolution> B = new ArrayList<>(1);
                B.add(solution);
                int flag = (new DominanceComparator<DoubleSolution>()).compare(A.get(0), B.get(0));
                if (flag < 0) {
                    value = -this.calculateHypervolumeIndicator(A.get(0), B.get(0), problem.getNumberOfObjectives(), maximumValues, minimumValues);
                } else {
                    value = this.calculateHypervolumeIndicator(B.get(0), A.get(0), problem.getNumberOfObjectives(), maximumValues, minimumValues);
                }
                if (Double.isNaN(value)) {
                    value = 0.0;
                }

                if (value != 0.0 && Math.abs(value) > this.maxIndicatorValue) {
                    this.maxIndicatorValue = Math.abs(value);
                }

                aux.add(value);
            }

            this.indicatorValues.add(aux);
        }

    }

    public double fitness(List<DoubleSolution> population, int position) {
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
    public void calculate(List<DoubleSolution> population, Problem<DoubleSolution> problem) {
        List<Double> maximumValues = new ArrayList<>(problem.getNumberOfObjectives());
        List<Double> minimumValues = new ArrayList<>(problem.getNumberOfObjectives());

        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            maximumValues.add(-Double.MAX_VALUE);
            minimumValues.add(Double.MAX_VALUE);
        }

        population.forEach(solution -> {
            for(int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
                double value = solution.objectives()[obj];
                maximumValues.set(obj, Math.max(value, maximumValues.get(obj)));
                minimumValues.set(obj, Math.min(value, minimumValues.get(obj)));
            }
            solution.attributes().put(Fitness.class, 0.0);
        });

        computeIndicatorValuesHD(population, problem, maximumValues, minimumValues);

        for(int position = 0; position < population.size(); position++) {
            population.get(position).attributes().put(Fitness.class, fitness(population, position));
        }
    }


}
