package pl.edu.agh.cea.algorithms;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BestSolutionSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;
import pl.edu.agh.cea.fitness.FitnessCalculator;
import pl.edu.agh.cea.observation.Subscriber;
import pl.edu.agh.cea.utils.AwardedSolutionSelector;

import java.util.ArrayList;
import java.util.List;

public class RewrittenMOCellBuilder<S extends Solution<?>> implements AlgorithmBuilder<MOCell<S>> {
    private final Problem<S> problem;
    private final CrossoverOperator<S> crossoverOperator;
    private final MutationOperator<S> mutationOperator;
    private int maxEvaluations;
    private int populationSize;
    private Neighborhood<S> neighborhood;
    private BoundedArchive<S> archive;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;
    private FitnessCalculator<S> fitnessCalculator;
    private final List<Subscriber> algorithmFitnessSubscribers;
    private final List<Subscriber> algorithmHyperVolumeSubscribers;

    public RewrittenMOCellBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.maxEvaluations = 25000;
        this.populationSize = 101;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new BestSolutionSelection<>(new FitnessComparator<>());
        this.neighborhood = new C9<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
        this.evaluator = new SequentialSolutionListEvaluator<>();
        this.archive = new CrowdingDistanceArchive<>(this.populationSize);
        this.algorithmFitnessSubscribers = new ArrayList<>();
        this.algorithmHyperVolumeSubscribers = new ArrayList<>();
    }

    public RewrittenMOCellBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("MaxEvaluations is negative: " + maxEvaluations);
        } else {
            this.maxEvaluations = maxEvaluations;
            return this;
        }
    }

    public RewrittenMOCellBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        } else {
            this.populationSize = populationSize;
            this.neighborhood = new C9<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
            this.archive = new CrowdingDistanceArchive<>(this.populationSize);
            return this;
        }
    }

    public RewrittenMOCellBuilder<S> setArchive(BoundedArchive<S> archive) {
        this.archive = archive;
        return this;
    }

    public RewrittenMOCellBuilder<S> setNeighborhood(Neighborhood<S> neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public RewrittenMOCellBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("SelectionOperator is null");
        } else {
            this.selectionOperator = selectionOperator;
            return this;
        }
    }

    public RewrittenMOCellBuilder<S> setEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("Evaluator is null");
        } else {
            this.evaluator = evaluator;
            return this;
        }
    }

    public RewrittenMOCellBuilder<S> setFitnessCalculator(FitnessCalculator<S> fitnessCalculator) {
        this.fitnessCalculator = fitnessCalculator;
        return this;
    }

    public RewrittenMOCellBuilder<S> setAlgorithmHyperVolumeObservers(Subscriber ... subscribers) {
        this.algorithmHyperVolumeSubscribers.addAll(List.of(subscribers));
        return this;
    }

    public RewrittenMOCellBuilder<S> setAlgorithmFitnessObservers(Subscriber ... subscribers) {
        this.algorithmFitnessSubscribers.addAll(List.of(subscribers));
        return this;
    }

    @Override
    public RewrittenMOCell<S> build() {
        RewrittenMOCell<S> RewrittenMOCell = new RewrittenMOCell<>(problem, maxEvaluations, populationSize,
                archive, neighborhood, crossoverOperator, mutationOperator,
                selectionOperator, evaluator, fitnessCalculator);

        algorithmFitnessSubscribers.forEach(RewrittenMOCell::addFitnessSubscriber);
        algorithmHyperVolumeSubscribers.forEach(RewrittenMOCell::addHyperVolumeSubscriber);
        return RewrittenMOCell;
    }
}
