package pl.edu.agh.cea.runner;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
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
import pl.edu.agh.cea.observation.TypicalFitnessObserver;
import pl.edu.agh.cea.operator.AdjacencyMutationOperator;
import pl.edu.agh.cea.operator.AdjacencyPolynomialMutation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Cellular Evolutionary Algorithm sociocognitive scenario
 */
public class AdjacencyDoubleMOCellRunner extends AbstractAlgorithmRunner {
    public AdjacencyDoubleMOCellRunner() {
    }

    public static void main(String[] args) throws JMetalException, IOException, PythonExecutionException {
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

        TypicalFitnessObserver fitnessObserver = new TypicalFitnessObserver();

        Algorithm<List<AdjacencyDoubleSolution>> algorithm = new AdjacencyMOCellBuilder(problem, crossover, mutation)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive<>(100))
                .setFitnessCalculator(new AdjacencyDoubleFitnessCalculator())
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

        List<Double> avgHistory = fitnessObserver.getAveragesPerEpoch();

        // @TODO visualize something
        List<Double> x = NumpyUtils.linspace(0, avgHistory.size(), avgHistory.size());
        List<Double> y = avgHistory;

        Plot plt = Plot.create();
        plt.plot().add(x, y, "o").label("Fitness");
        plt.legend().loc("upper right");
        plt.title("Avg. fitness per iterations");
        plt.show();
    }
}
