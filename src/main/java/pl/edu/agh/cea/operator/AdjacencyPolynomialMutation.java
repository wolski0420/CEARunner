package pl.edu.agh.cea.operator;

import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;
import pl.edu.agh.cea.model.solution.AdjacencySolution;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * One of the most important tactic in AdjacencyMOCell mechanism
 * Extending there defines, what type of variables we want to use
 * Implementing there obligates us to hold iterAwardedSolutions
 * Mentioned set is used to check if solution has neighbour specially awarded
 */
public class AdjacencyPolynomialMutation extends PolynomialMutation implements AdjacencyMutationOperator<DoubleSolution> {
    private final Random random = new Random();
    private Set<? extends AdjacencySolution<?, ?>> setOfIterAwardedSolutions;
    private final double mutationProbability;
    private final double distributionIndex;
    private final RepairDoubleSolution solutionRepair;
    private final RandomGenerator<Double> randomGenerator;

    public AdjacencyPolynomialMutation(double mutationProbability, double distributionIndex) {
        super(mutationProbability, distributionIndex);
        this.mutationProbability = mutationProbability;
        this.distributionIndex = distributionIndex;
        this.solutionRepair = new RepairDoubleSolutionWithBoundValue();
        this.randomGenerator = () -> JMetalRandom.getInstance().nextDouble();
    }

    @Override
    public void setIterAwardedSolutions(Set<? extends AdjacencySolution<?, ?>> newIterAwardedSolutions) {
        this.setOfIterAwardedSolutions = newIterAwardedSolutions;
    }

    @Override
    public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
        this.doMutation(solution);
        return solution;
    }

    /**
     * Extended mutation process, possible paths:
     * 1. Given solution has a neighbour who is specially awarded -> mutate using its genes.
     * 2. Given solution does not have a specially awarded neighbour -> classic mutate solo
     * @param solution - given individual
     */
    public void doMutation(DoubleSolution solution) {
        Set<? extends AdjacencySolution<?, ?>> awardedNeighbours = ((AdjacencyDoubleSolution) solution).getNeighbours().stream()
                .filter(setOfIterAwardedSolutions::contains)
                .collect(Collectors.toSet());

        if (!awardedNeighbours.isEmpty()) {
            AdjacencySolution<?, ?> chosenAwardedNeighbour = new ArrayList<>(awardedNeighbours).get(random.nextInt(awardedNeighbours.size()));

            int randomIndex = random.nextInt(Math.min(solution.variables().size(), chosenAwardedNeighbour.variables().size()));
            for(int i = 0; i < solution.variables().size(); ++i) {
                if (i == randomIndex) {
                    // random variable is going to be copied from neighbour
                    solution.variables().set(randomIndex, (Double) chosenAwardedNeighbour.variables().get(randomIndex));
                } else {
                    double y = solution.variables().get(i);
                    double yl = solution.getBounds(i).getLowerBound();
                    double yu = solution.getBounds(i).getUpperBound();
                    if (yl == yu) {
                        y = yl;
                    } else {
                        double delta1 = (y - yl) / (yu - yl);
                        double delta2 = (yu - y) / (yu - yl);
                        double rnd = this.randomGenerator.getRandomValue();
                        double mutPow = 1.0 / (this.distributionIndex + 1.0);
                        double deltaq;
                        double val;
                        double xy;
                        if (rnd <= 0.5) {
                            xy = 1.0 - delta1;
                            val = 2.0 * rnd + (1.0 - 2.0 * rnd) * Math.pow(xy, this.distributionIndex + 1.0);
                            deltaq = Math.pow(val, mutPow) - 1.0;
                        } else {
                            xy = 1.0 - delta2;
                            val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * Math.pow(xy, this.distributionIndex + 1.0);
                            deltaq = 1.0 - Math.pow(val, mutPow);
                        }

                        y += deltaq * (yu - yl);
                        y = this.solutionRepair.repairSolutionVariableValue(y, yl, yu);
                    }
                    solution.variables().set(i, y);
                }
            }
        } else {
            for(int i = 0; i < solution.variables().size(); ++i) {
                if (this.randomGenerator.getRandomValue() <= this.mutationProbability) {
                    double y = solution.variables().get(i);
                    double yl = solution.getBounds(i).getLowerBound();
                    double yu = solution.getBounds(i).getUpperBound();
                    if (yl == yu) {
                        y = yl;
                    } else {
                        double delta1 = (y - yl) / (yu - yl);
                        double delta2 = (yu - y) / (yu - yl);
                        double rnd = this.randomGenerator.getRandomValue();
                        double mutPow = 1.0 / (this.distributionIndex + 1.0);
                        double deltaq;
                        double val;
                        double xy;
                        if (rnd <= 0.5) {
                            xy = 1.0 - delta1;
                            val = 2.0 * rnd + (1.0 - 2.0 * rnd) * Math.pow(xy, this.distributionIndex + 1.0);
                            deltaq = Math.pow(val, mutPow) - 1.0;
                        } else {
                            xy = 1.0 - delta2;
                            val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * Math.pow(xy, this.distributionIndex + 1.0);
                            deltaq = 1.0 - Math.pow(val, mutPow);
                        }

                        y += deltaq * (yu - yl);
                        y = this.solutionRepair.repairSolutionVariableValue(y, yl, yu);
                    }

                    solution.variables().set(i, y);
                }
            }
        }
    }
}
