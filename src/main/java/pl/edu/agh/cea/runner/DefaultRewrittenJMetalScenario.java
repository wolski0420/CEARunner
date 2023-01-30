package pl.edu.agh.cea.runner;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Schaffer;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import pl.edu.agh.cea.algorithms.RewrittenMOCellBuilder;
import pl.edu.agh.cea.fitness.DoubleFitnessCalculator;
import pl.edu.agh.cea.observation.TypicalFitnessObserver;
import pl.edu.agh.cea.observation.TypicalHyperVolumeObserver;
import pl.edu.agh.cea.utils.ResultsPlotter;

import java.util.List;


public class DefaultRewrittenJMetalScenario extends AbstractAlgorithmRunner {
    public DefaultRewrittenJMetalScenario() {
    }

    public static void main(String[] args) {
        Problem<DoubleSolution> problem = new Schaffer();
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

        MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        TypicalFitnessObserver fitnessObserver = new TypicalFitnessObserver();
        TypicalHyperVolumeObserver hyperVolumeObserver = new TypicalHyperVolumeObserver();

        BoundedArchive<DoubleSolution> archive = new HypervolumeArchive(100, new PISAHypervolume());

        Algorithm<List<DoubleSolution>> algorithm = new RewrittenMOCellBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(25000)
                .setPopulationSize(100)
                .setArchive(new CrowdingDistanceArchive(100))
                .setFitnessCalculator(new DoubleFitnessCalculator())
                .setAlgorithmFitnessObservers(fitnessObserver)
                .setAlgorithmHyperVolumeObservers(hyperVolumeObserver)
                .setArchive(archive)
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
        resultsPlotter.plotHyperVolumeAvgPerEpoch(hyperVolumeObserver.getAveragesPerEpoch(),
                DefaultRewrittenJMetalScenario.class.getSimpleName());
    }
}
