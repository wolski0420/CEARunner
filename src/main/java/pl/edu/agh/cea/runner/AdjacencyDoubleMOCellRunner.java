package pl.edu.agh.cea.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import pl.edu.agh.cea.algorithms.AdjacencyMOCellBuilder;
import pl.edu.agh.cea.fitness.AdjacencyDoubleFitnessCalculator;
import pl.edu.agh.cea.model.solution.AdjacencyDoubleSolution;
import pl.edu.agh.cea.operator.AdjacencyMutationOperator;
import pl.edu.agh.cea.operator.AdjacencyPolynomialMutation;

import java.io.FileNotFoundException;
import java.util.List;

/**
 *  Cellular Evolutionary Algorithm sociocognitive scenario
 */
public class AdjacencyDoubleMOCellRunner extends AbstractAlgorithmRunner {
    public AdjacencyDoubleMOCellRunner() {
    }

    public static void main(String[] args) throws JMetalException, FileNotFoundException {
        String referenceParetoFront = "";
        String problemName;

        if (args.length == 1) {
            problemName = args[0];
        } else if (args.length == 2) {
            problemName = args[0];
            referenceParetoFront = args[1];
        } else {
            problemName = "pl.edu.agh.cea.problems.AdjacencyDoubleZDT6";
            referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.csv";
        }

        Problem<AdjacencyDoubleSolution> problem = ProblemUtils.loadProblem(problemName);

        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        double mutationProbability = 1.0 / (double)problem.getNumberOfVariables();
        double mutationDistributionIndex = 20.0;
        AdjacencyMutationOperator<DoubleSolution> mutation = new AdjacencyPolynomialMutation(mutationProbability, mutationDistributionIndex);

        Algorithm<List<AdjacencyDoubleSolution>> algorithm = new AdjacencyMOCellBuilder(problem, crossover, mutation)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive<>(100))
                .setFitnessCalculator(new AdjacencyDoubleFitnessCalculator())
                .build();
        AlgorithmRunner algorithmRunner = (new AlgorithmRunner.Executor(algorithm)).execute();
        List<AdjacencyDoubleSolution> population = algorithm.getResult();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

    }
}
