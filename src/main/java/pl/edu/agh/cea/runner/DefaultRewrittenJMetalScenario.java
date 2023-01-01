package pl.edu.agh.cea.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.re.RE21;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import pl.edu.agh.cea.algorithms.RewrittenMOCellBuilder;
import pl.edu.agh.cea.fitness.DoubleFitnessCalculator;
import pl.edu.agh.cea.observation.TypicalFitnessObserver;
import pl.edu.agh.cea.utils.ResultsPlotter;

import java.util.List;
import java.util.stream.Collectors;


public class DefaultRewrittenJMetalScenario extends AbstractAlgorithmRunner {
    public DefaultRewrittenJMetalScenario() {
    }

    public static void main(String[] args) {
        Problem<DoubleSolution> problem = new RE21();
        String referenceParetoFront = "resources/referenceFrontsCSV/RE21.csv";

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

        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        TypicalFitnessObserver fitnessObserver = new TypicalFitnessObserver();

        Algorithm<List<DoubleSolution>> algorithm = new RewrittenMOCellBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive(100))
                .setFitnessCalculator(new DoubleFitnessCalculator())
                .setAlgorithmObservers(fitnessObserver)
                .build();
        AlgorithmRunner algorithmRunner = (new AlgorithmRunner.Executor(algorithm)).execute();
        List<DoubleSolution> population = algorithm.getResult();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

        ResultsPlotter resultsPlotter = new ResultsPlotter();
        resultsPlotter.plotFitnessAvgPerEpoch(fitnessObserver.getAveragesPerEpoch().stream()
                .filter(value -> !value.isNaN())
                .collect(Collectors.toList()));
    }
}
