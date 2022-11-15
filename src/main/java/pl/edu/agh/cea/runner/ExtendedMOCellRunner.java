package pl.edu.agh.cea.runner;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import pl.edu.agh.cea.algorithms.ExtendedMOCellBuilder;
import pl.edu.agh.cea.operator.ExtendedBitFlipMutation;
import pl.edu.agh.cea.operator.ExtendedMutationOperator;

public class ExtendedMOCellRunner extends AbstractAlgorithmRunner {
    public ExtendedMOCellRunner() {
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
            problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT5";
            referenceParetoFront = "";
        }

        Problem<BinarySolution> problem = ProblemUtils.loadProblem(problemName);

        double crossoverProbability = 0.9;
        CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover(crossoverProbability);

        double mutationProbability = 1.0 / (double)problem.getNumberOfVariables();
        ExtendedMutationOperator<BinarySolution> mutation = new ExtendedBitFlipMutation(mutationProbability);

        Algorithm<List<BinarySolution>> algorithm = (new ExtendedMOCellBuilder(problem, crossover, mutation)).setMaxEvaluations(25000).setPopulationSize(100).setArchive(new CrowdingDistanceArchive(100)).build();
        AlgorithmRunner algorithmRunner = (new AlgorithmRunner.Executor(algorithm)).execute();
        List<BinarySolution> population = algorithm.getResult();

        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

    }
}
