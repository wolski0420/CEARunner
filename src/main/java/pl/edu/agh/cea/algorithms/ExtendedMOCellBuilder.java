package pl.edu.agh.cea.algorithms;

import java.util.List;

import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BestSolutionSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.FitnessComparator;
import org.uma.jmetal.util.comparator.StrengthFitnessComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import pl.edu.agh.cea.model.neighbourhood.Extended2DMesh;
import pl.edu.agh.cea.operator.ExtendedMutationOperator;
import pl.edu.agh.cea.utils.AwardedSolutionSelector;

public class ExtendedMOCellBuilder<S extends Solution<?>> implements AlgorithmBuilder<ExtendedMOCell<S>> {
    protected final Problem<S> problem;
    protected int maxEvaluations;
    protected int populationSize;
    protected CrossoverOperator<S> crossoverOperator;
    protected ExtendedMutationOperator<S> mutationOperator;
    protected SelectionOperator<List<S>, S> selectionOperator;
    protected SolutionListEvaluator<S> evaluator;
    protected Neighborhood<S> neighborhood;
    protected BoundedArchive<S> archive;
    protected AwardedSolutionSelector<S> awardedSolutionSelector;

    public ExtendedMOCellBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, ExtendedMutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.maxEvaluations = 25000;
        this.populationSize = 101;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new BestSolutionSelection<>(new StrengthFitnessComparator<>());
        this.neighborhood = new Extended2DMesh<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
        this.evaluator = new SequentialSolutionListEvaluator<>();
        this.archive = new CrowdingDistanceArchive<>(this.populationSize);
        this.awardedSolutionSelector = new AwardedSolutionSelector<>(new StrengthFitnessComparator<>(), 0.1, 0.01, 0.02);
    }

    public ExtendedMOCellBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        } else {
            this.maxEvaluations = maxEvaluations;
            return this;
        }
    }

    public ExtendedMOCellBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        } else {
            this.populationSize = populationSize;
            this.neighborhood = new Extended2DMesh<>((int) Math.sqrt(this.populationSize), (int) Math.sqrt(this.populationSize));
            this.archive = new CrowdingDistanceArchive<>(this.populationSize);
            return this;
        }
    }

    public ExtendedMOCellBuilder<S> setArchive(BoundedArchive<S> archive) {
        this.archive = archive;
        return this;
    }

    public ExtendedMOCellBuilder<S> setNeighborhood(Neighborhood<S> neighborhood) {
        this.neighborhood = neighborhood;
        return this;
    }

    public ExtendedMOCellBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("selectionOperator is null");
        } else {
            this.selectionOperator = selectionOperator;
            return this;
        }
    }

    public ExtendedMOCellBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        } else {
            this.evaluator = evaluator;
            return this;
        }
    }

    public ExtendedMOCell<S> build() {
        return new ExtendedMOCell<>(this.problem, this.maxEvaluations, this.populationSize, this.archive, this.neighborhood, this.crossoverOperator, this.mutationOperator, this.selectionOperator, this.evaluator, this.awardedSolutionSelector);
    }

    public Problem<S> getProblem() {
        return this.problem;
    }

    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public BoundedArchive<S> getArchive() {
        return this.archive;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return this.crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return this.mutationOperator;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return this.selectionOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return this.evaluator;
    }

    public AwardedSolutionSelector<S> getAwardedSolutionSelector() {
        return awardedSolutionSelector;
    }

    public void setAwardedSolutionSelector(AwardedSolutionSelector<S> awardedSolutionSelector) {
        this.awardedSolutionSelector = awardedSolutionSelector;
    }

    public static enum MOCellVariant {
        MOCell,
        SteadyStateMOCell,
        Measures;

        private MOCellVariant() {
        }
    }
}