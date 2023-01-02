package pl.edu.agh.cea.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import pl.edu.agh.cea.algorithms.AdjacencyMOCellBuilder;
import pl.edu.agh.cea.fitness.DoubleFitnessCalculator;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;
import pl.edu.agh.cea.observation.TypicalFitnessObserver;
import pl.edu.agh.cea.operator.AdjacencyMutationOperator;
import pl.edu.agh.cea.operator.AdjacencyPolynomialMutation;
import pl.edu.agh.cea.problems.AdjacencyDoubleLSMOP9;
import pl.edu.agh.cea.problems.AdjacencyDoubleSchaffer;
import pl.edu.agh.cea.utils.ResultsPlotter;

import java.util.List;

/**
 *  Cellular Evolutionary Algorithm sociocognitive scenario
 */
public class AdjacencyDoubleMOCellRunner extends AbstractAlgorithmRunner {
    public AdjacencyDoubleMOCellRunner() {
    }

    public static void main(String[] args) {
        // @TODO benchmarks: Sphere/Dejong, Ackley, Rastrigin, Griewang, Schweffel (Schaffer?)
        // @TODO check if there is a possibility to choose single or multi criteria
        Problem<DoubleSolution> problem = new AdjacencyDoubleLSMOP9();
        String referenceParetoFront = "";

        if (args.length == 1) {
            problem = ProblemUtils.loadProblem(args[0]);
        } else if (args.length == 2) {
            problem = ProblemUtils.loadProblem(args[0]);
            referenceParetoFront = args[1];
        }

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / (double)problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        AdjacencyMutationOperator<DoubleSolution> mutation = new AdjacencyPolynomialMutation(mutationProbability, mutationDistributionIndex);

        TypicalFitnessObserver fitnessObserver = new TypicalFitnessObserver();

        Algorithm<List<AdjacencyDoubleSolution>> algorithm = new AdjacencyMOCellBuilder(problem, crossover, mutation)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive<>(100))
                .setFitnessCalculator(new DoubleFitnessCalculator())
                .setAlgorithmObservers(fitnessObserver)
                .build();
        AlgorithmRunner algorithmRunner = (new AlgorithmRunner.Executor(algorithm)).execute();
        List<AdjacencyDoubleSolution> population = algorithm.getResult();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

        ResultsPlotter resultsPlotter = new ResultsPlotter();
        resultsPlotter.plotFitnessAvgPerEpoch(fitnessObserver.getAveragesPerEpoch(),
                AdjacencyDoubleMOCellRunner.class.getSimpleName());
    }
}
