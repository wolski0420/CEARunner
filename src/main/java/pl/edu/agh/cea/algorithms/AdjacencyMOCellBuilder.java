package pl.edu.agh.cea.algorithms;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BestSolutionSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import pl.edu.agh.cea.fitness.FitnessCalculator;
import pl.edu.agh.cea.model.neighbourhood.AdjacencyMaintainer;
import pl.edu.agh.cea.model.solution.AdjacencySolution;
import pl.edu.agh.cea.observation.Subscriber;
import pl.edu.agh.cea.operator.AdjacencyMutationOperator;
import pl.edu.agh.cea.utils.AwardedSolutionSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Simply builder for AdjacencyMOCell
 * @param <S> - type of AdjacencySolution
 */
public class AdjacencyMOCellBuilder<S extends AdjacencySolution<S, ?>> implements AlgorithmBuilder<AdjacencyMOCell<S>> {
    private final Problem<S> problem;
    private final CrossoverOperator<S> crossoverOperator;
    private final AdjacencyMutationOperator<S> mutationOperator;
    private int maxEvaluations;
    private int populationSize;
    private BoundedArchive<S> archive;
    private AdjacencyMaintainer<S> neighborhood;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;
    private AwardedSolutionSelector<S> awardSelector;
    private FitnessCalculator<S> fitnessCalculator;
    private final List<Subscriber> algorithmFitnessSubscribers;
    private final List<Subscriber> algorithmHyperVolumeSubscribers;

    public AdjacencyMOCellBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, AdjacencyMutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.maxEvaluations = 25000;
        this.populationSize = 101;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new BestSolutionSelection<>(new FitnessComparator<>());
        this.neighborhood = new AdjacencyMaintainer<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
        this.evaluator = new SequentialSolutionListEvaluator<>();
        this.archive = new CrowdingDistanceArchive<>(this.populationSize);
        this.awardSelector = new AwardedSolutionSelector<>(new FitnessComparator<>(), 0.1, 0.01, 0.02);
        this.algorithmFitnessSubscribers = new ArrayList<>();
        this.algorithmHyperVolumeSubscribers = new ArrayList<>();
    }

    public AdjacencyMOCellBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("MaxEvaluations is negative: " + maxEvaluations);
        } else {
            this.maxEvaluations = maxEvaluations;
            return this;
        }
    }

    public AdjacencyMOCellBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        } else {
            this.populationSize = populationSize;
            this.neighborhood = new AdjacencyMaintainer<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
            this.archive = new CrowdingDistanceArchive<>(this.populationSize);
            return this;
        }
    }

    public AdjacencyMOCellBuilder<S> setArchive(BoundedArchive<S> archive) {
        this.archive = archive;
        return this;
    }

    public AdjacencyMOCellBuilder<S> setNeighborhood(AdjacencyMaintainer<S> neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public AdjacencyMOCellBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("SelectionOperator is null");
        } else {
            this.selectionOperator = selectionOperator;
            return this;
        }
    }

    public AdjacencyMOCellBuilder<S> setEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("Evaluator is null");
        } else {
            this.evaluator = evaluator;
            return this;
        }
    }

    public AdjacencyMOCellBuilder<S> setAwardSelector(AwardedSolutionSelector<S> awardSelector) {
        this.awardSelector = awardSelector;
        return this;
    }

    public AdjacencyMOCellBuilder<S> setFitnessCalculator(FitnessCalculator<S> fitnessCalculator) {
        this.fitnessCalculator = fitnessCalculator;
        return this;
    }

    public AdjacencyMOCellBuilder<S> setAlgorithmFitnessObservers(Subscriber ... subscribers) {
        this.algorithmFitnessSubscribers.addAll(List.of(subscribers));
        return this;
    }

    public AdjacencyMOCellBuilder<S> setAlgorithmHyperVolumeObservers(Subscriber ... subscribers) {
        this.algorithmHyperVolumeSubscribers.addAll(List.of(subscribers));
        return this;
    }

    @Override
    public AdjacencyMOCell<S> build() {
        AdjacencyMOCell<S> adjacencyMOCell = new AdjacencyMOCell<>(problem, maxEvaluations, populationSize,
                archive, neighborhood, crossoverOperator, mutationOperator,
                selectionOperator, evaluator, awardSelector, fitnessCalculator);

        algorithmFitnessSubscribers.forEach(adjacencyMOCell::addFitnessSubscriber);
        algorithmHyperVolumeSubscribers.forEach(adjacencyMOCell::addHyperVolumeSubscriber);
        return adjacencyMOCell;
    }
}